package com.alunw.moany.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import com.alunw.moany.model.User;
import com.alunw.moany.model.UserAuthority;
import com.alunw.moany.model.UserAuthorityTypes;
import com.alunw.moany.repository.UserAuthorityRepository;
import com.alunw.moany.repository.UserRepository;

/**
 * TODO Experimental service, to handle database initialization and migration (update/rollback).
 * 
 * Should only be called from application CommandLineRunner.
 *
 */ 
@Component
public class DatabaseDefinitionService {
	
	public static final String HIBERNATE_DIALECT_KEY = "hibernate.dialect";
	public static final String HIBERNATE_DIALECT_MYSQL_8 = "MySQL8";
	public static final String HIBERNATE_DIALECT_H2 = "H2";
	public static final String HIBERNATE_AUTO_KEY = "hibernate.hbm2ddl.auto";
	public static final String CREATE_DROP = "create-drop";
	public static final String MYSQL_SCRIPT_PATH = "sql/";
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseDefinitionService.class);
	
	@Value("${moany.admin.username:admin}") // default admin user
	private String adminUsername;
	
	@Value("${moany.admin.password:}") // default empty password - generated password will appear in log messages
	private String adminPassword;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserAuthorityRepository authorityRepo;
	
	/**
	 * Since Spring Boot 2.2 have needed to revise the identification of the 
	 * database dialect. If not set in application.yml, HIBERNATE_DIALECT appears
	 * as null - so if this value is null we will treat as (local) H2 database.
	 * 
	 * @throws SQLException
	 */
	@PostConstruct
	public void initializeDatabase() throws SQLException {
		
		logger.info("Checking database is ready...");
		
		EntityManagerFactory emf = em.getEntityManagerFactory();
		Map<String, Object> properties = emf.getProperties();
		
		String hibernateDialect = (String) properties.get(HIBERNATE_DIALECT_KEY);
		if (hibernateDialect == null) {
			hibernateDialect = HIBERNATE_DIALECT_H2;
			logger.debug(HIBERNATE_DIALECT_KEY + " is null. Assuming " + hibernateDialect + ".");
		} else {
			logger.debug(HIBERNATE_DIALECT_KEY + " = " + hibernateDialect);
		}
		
		String hibernateAuto = (String) properties.get(HIBERNATE_AUTO_KEY);
		logger.debug(HIBERNATE_AUTO_KEY + " = " + hibernateAuto);
		
		// TODO factor out vendor specific code - and check for valid dialects
		try {
			if (hibernateDialect != null && hibernateDialect.contains("H2")) {
				initH2(hibernateDialect, hibernateAuto);
			} else if (hibernateDialect != null && hibernateDialect.contains("MySQL")) {
				initMySql(hibernateDialect, hibernateAuto);
			} else {
				throw new Exception("Application is only designed to use H2 or MySQL database. Please check the configuration and retry.");
			}
		} catch (Exception e) {
			shutdown(e);
		}
		
		// TODO create admin account
		
		// check whether admin user exists
		User adminUser = userRepo.findByUsername(adminUsername);
		if (adminUser == null) {
			createAdminUser(userRepo, authorityRepo);
		} else {
			logger.info("Admin account found in datastore - skipping creation of initial account...");
		}
		
	}
	
	/**
	 * TODO MySQL specific
	 * 
	 * Returns current database version. If no version is found, it returns 0.
	 * 
	 * If no version is found but database tables exist, then an exception is thrown (to stop any existing data being 
	 * lost by re-creating the database).
	 * 
	 * @return
	 */
	private int mysqlDbVersion() throws Exception {
		
		// get tables
		Query queryTables = em.createNativeQuery(
			"select table_schema, table_name from information_schema.tables where table_schema = 'moany'"); // TODO factor out?
		List<Object[]> results = queryTables.getResultList();
		if (results == null) {
			throw new Exception("Null returned querying database tables.");
		}
		if (results.isEmpty()) {
			logger.info("No tables found - create database from scratch...");
			return 0;
		} else {
			// check + list tables found
			boolean hasSystemInfo = false;
			for (Object[] row : results) {
				logger.debug("Found table: {}.{}", row[0], row[1]);
				if ("system_info".equals(row[1])) {
					hasSystemInfo = true;
				}
			}
			if (!hasSystemInfo) {
				throw new Exception("Tables found in database, but no version data.");
			}
		}
		
		// get version
		Query versionQuery = em.createNativeQuery("select value from system_info where name = 'db_version'");
		Object result = versionQuery.getSingleResult();
		int version = 0;
		try {
			version = Integer.parseInt((String) result);
		} catch (NumberFormatException e) {
			throw new Exception("Unable to parse database version [" + result + "]");
		}
		return version;
	}
	
	
	private void shutdown(Exception e) {
		logger.info("********************************************************************************************************************");
		logger.info("");
		logger.info("Unable to initialize database, shutting down... [{}]", e.getMessage());
		logger.info("");
		logger.info("********************************************************************************************************************");
		System.exit(-1);
	}
	
	/*
	 * Initialize H2 database
	 * 
	 * TODO factor out as separate bean to allow other databases to be easily added.
	 */
	private void initH2(String hibernateDialect, String hibernateAuto) throws Exception {
		
		logger.info("Using H2 database...");
		
		if (hibernateAuto == null || !CREATE_DROP.equals(hibernateAuto)) {
			throw new Exception("H2 database can only be used with " + HIBERNATE_AUTO_KEY + "=" + CREATE_DROP);
		}
		// Don't need to create/update H2 database
	}
	
	/*
	 * Initialize MySQL database
	 * 
	 * TODO need conventions for finding database scripts. Should all be on classpath under 'sql' folder.
	 * 
	 * TODO factor out as separate bean to allow other databases to be easily added.
	 */
	private void initMySql(String hibernateDialect, String hibernateAuto) throws Exception {
		
		logger.info("Using MySQL database...");
		
		// MySQL dialect can be used with any auto value, but if not set check if database exists, and create/update if required
		if (hibernateAuto != null && !hibernateAuto.isEmpty()) {
			throw new Exception("MySQL database can only be used without " + HIBERNATE_AUTO_KEY + " set.");
		}
		
		int currentDbVersion = mysqlDbVersion();
		logger.debug("current database version = {}", currentDbVersion);
		
		// TODO move mysql scripts to separate sub-folder
		TreeMap<Integer, Resource> updateScripts = getVersionedScripts("classpath:" + MYSQL_SCRIPT_PATH + "mysql-update-v*.sql", "^mysql-update-v([0-9]+).sql$");
		// TODO executed update/rollback commands should be stored in database to allow rollback
		TreeMap<Integer, Resource> rollbackScripts = getVersionedScripts("classpath:" + MYSQL_SCRIPT_PATH + "mysql-rollback-v*.sql", "^mysql-rollback-v([0-9]+).sql$");
		
		if (updateScripts.isEmpty()) {
			throw new Exception("No MySQL database update scripts found.");
		}
		
		if (rollbackScripts.isEmpty()) {
			throw new Exception("No MySQL database rollback scripts found.");
		}
		
		if (updateScripts.size() != rollbackScripts.size()) {
			throw new Exception("Number of MySQL update and rollback scripts differ.");
		}
		
		int codeDbVersion = updateScripts.descendingKeySet().iterator().next();
		logger.debug("code database version = {}", codeDbVersion);
		
		if (codeDbVersion > currentDbVersion) {
			logger.info("Updating database version... [v{} - > v{}]", currentDbVersion, codeDbVersion);
			for (Entry<Integer, Resource> script : updateScripts.entrySet()) {
				if (currentDbVersion < script.getKey().intValue()) {
					mysqlExecuteScript(script.getValue());
				} else {
					logger.info("...skipping database update script: {}", script.getValue().getFilename());
				}
			}
		} else if (currentDbVersion > codeDbVersion) {
			logger.info("Rolling back database version... [v{} - > v{}]", currentDbVersion, codeDbVersion);
			// TODO rollback steps must have been stored in database
			throw new Exception("Rollback datbase changes not yet implmented - must rollback manually.");
		} else {
			logger.info("Database is up-to-date. [v{}]", currentDbVersion);
		}
	}
	
	private void mysqlExecuteScript(Resource script) throws Exception {
		
		logger.info("Running database script: {}", script.getFilename());
		String sql = getResourceAsString(script);
		logger.debug("Update SQL:\n{}\n", sql);
		
		Connection c = dataSource.getConnection();
		c.setAutoCommit(false);
		Statement statement = c.createStatement();
		statement.executeUpdate(sql);
		statement.close();
		c.commit();
		c.close();
	}
	
	private TreeMap<Integer, Resource> getVersionedScripts(String resourcePattern, String versionMatchingPattern) throws IOException {
		
		Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(resourcePattern);
		Pattern pattern = Pattern.compile(versionMatchingPattern);
		
		TreeMap<Integer, Resource> scripts = new TreeMap<>();
		for (Resource resource : resources) {
			
			logger.debug("resource found: {}", resource.getFilename());
			
			Matcher matcher = pattern.matcher(resource.getFilename());
			if (matcher.find()) {
				Integer key = Integer.valueOf(matcher.group(1));
				scripts.put(key, resource);
			} else {
				logger.warn("Unable to extract version data from script name: {}", resource.getFilename());
			}
		}
		
		return scripts;
	}
	
	private String getResourceAsString(Resource resource) throws IOException {
		
		String resourcePath = MYSQL_SCRIPT_PATH + resource.getFilename();
		logger.debug("Getting resource as string: {}",  resourcePath);
		
		InputStream inputStream = null;
		StringBuilder data = new StringBuilder();
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			inputStream = classLoader.getResourceAsStream(resourcePath);
			
			try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
				String line;
				while ((line = br.readLine()) != null) {
					data.append(line).append("\n");
				}
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		
		return data.toString();
	}
	
	/**
	 * Create (admin) user account on initial data store creation. Use either configured username/password values, or
	 * or generate password for default user ('admin').
	 * 
	 * @param userRepo
	 * @param authorityRepo
	 */
	private void createAdminUser(UserRepository userRepo, UserAuthorityRepository authorityRepo) {
		
		String passwordMessage = "";
		if (adminPassword != null && adminPassword.length() > 0) {
			passwordMessage = "  Creating new '" + adminUsername + "' user with password from application configuration.";
		} else {
			adminPassword = generatePassword(12);
			passwordMessage = "  Creating new '" + adminUsername + "' user, password: " + adminPassword;
		}
		
		User user = new User(adminUsername, adminPassword);
		userRepo.save(user);
		
		UserAuthority authority = new UserAuthority(user, UserAuthorityTypes.USER.toString());
		authorityRepo.save(authority);
		
		authority = new UserAuthority(user, UserAuthorityTypes.ADMIN.toString());
		authorityRepo.save(authority);
		
		logger.info("****************************************************************************************");
		logger.info("");
		logger.info(passwordMessage);
		logger.info("");
		logger.info("****************************************************************************************");
	}
	
	/**
	 * Simple password generator - returning a string of lower-case letters of specified length.
	 * 
	 * @param targetStringLength
	 * @return String
	 */
	private String generatePassword(int targetStringLength) {
		
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		
		Random random = new Random();
		StringBuilder buffer = new StringBuilder(targetStringLength);
		for (int i = 0; i < targetStringLength; i++) {
			int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		
		return buffer.toString();
	}
}
