package com.alunw.moany.rest;

import com.alunw.moany.model.BarclaysCsvStatementV1;
import com.alunw.moany.model.NatWestCsvStatementV1;
import com.alunw.moany.model.Statement;
import com.alunw.moany.model.Transaction;
import com.alunw.moany.repository.TransactionRepository;
import com.alunw.moany.services.StatementService;
import com.alunw.moany.services.TransactionService;
import com.alunw.moany.services.validators.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/rest/statements/v2/")
public class RestStatementController {
	
	@Value("${moany.homeFolder}")
	private String homeFolder;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private StatementService statmentService;
	
	@Autowired
	private TransactionService transactionService;
	
	private static Logger logger = LoggerFactory.getLogger(RestStatementController.class);
	
	// TODO where to 'hold' importers? 
	public static final Statement[] STATEMENT_IMPORTERS = {
		new NatWestCsvStatementV1(),
		new BarclaysCsvStatementV1()
	};
	
	@PostConstruct
	public void init() {
		logger.info("init()");
	}
	
	/**
	 * Upload statement and display loaded transactions
	 *
	 * @param file The uploaded statement file.
	 * @param importerName Selected importer (name).
	 * @param autoAcc Create new accounts automatically?
	 * @param noDup Abort (and rollback) saving of statement if a duplicate transaction is found?
	 *        (If false, then duplicate transactions will be ignored, but other transactions, and accounts, will be saved.)
	 * @return ResponseEntity
	 */
	@PostMapping("/upload")
	public ResponseEntity<?> postStatementUpload(
			@RequestParam(name = "file", required = false) MultipartFile file, 
			@RequestParam(name = "importer", required = false) String importerName, 
			@RequestParam(name = "autoacc", required = false, defaultValue = "false") String autoAcc, 
			@RequestParam(name = "nodup", required = false, defaultValue = "false") String noDup) {
		
		logger.info("postStatementUpload(): importer = '" + importerName + "', autoAcc = '" + autoAcc + "', noDup = '" + noDup + "'");
		
		JsonResponse response = new JsonResponse();
		
		try {
			// Was file submitted?
			if (file == null) {
				String message = "No statement file submitted!";
				logger.error(message);
				response.setMessage(message);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
			
			// Was valid importer specified?
			Statement importer = getImporter(importerName);
			if (importer == null) {
				String message = "No valid statement type specified!";
				logger.error(message);
				response.setMessage(message);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
			
			// Check whether statement with this name has already been imported
			if (sourceExists(file.getOriginalFilename())) {
				String message = "A statement with the same name as the uploaded file has already been imported (" + file.getOriginalFilename() + "). File *NOT* imported.";
				logger.warn(message);
				response.setMessage(message);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			} 
			
			// Is file empty?
			if (file.isEmpty()) {
				String message = "File upload is empty - will not upload [" + file.getOriginalFilename() + "]";
				logger.warn(message);
				response.setMessage(message);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
			
			// Store uploaded file
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss-SSS"));
			Path dest = Paths.get(homeFolder, timestamp + ".upload");
			Files.copy(file.getInputStream(), dest);
			logger.info("File uploaded [" + dest.toString() + "]");
			
			// Parse uploaded file and save
			importer.parseStatement(dest.toFile());
			
			// TODO preview before save??
			
			int count = statmentService.save(importer, file.getOriginalFilename(), Boolean.parseBoolean(autoAcc), Boolean.parseBoolean(noDup));
			
			// Move/rename file to show uploaded
			Path renamed = Paths.get(homeFolder, timestamp + ".loaded");
			Files.move(dest, renamed);
			logger.info("File renamed [" + renamed.toString() + "]");
			
			response.setMessage("File processed with " + count + " transaction(s) uploaded and file archived as [" + renamed.toString() + "]");
			
		} catch (Exception e) {
			String message = "Exception uploading file [" + file.getOriginalFilename() + "]: " + e.getMessage();
			logger.error(message);
			response.setMessage(message);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
		
		// Update balances
		transactionService.recalculateBalances(null);
		
		List<Transaction> results = transactionRepo.findBySourceName(file.getOriginalFilename());
		response.setResult(results);
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/importers")
	public List<Statement> getStatementImporters() {
		return Arrays.asList(STATEMENT_IMPORTERS);
	}
	
	private boolean sourceExists(String source) {
		boolean result = false;
		
		List<Transaction> results = transactionRepo.findBySourceName(source);
		
		if (!results.isEmpty()) {
			result = true;
		}
		return result;
	}
	
	private Statement getImporter(String importerName) {
		
		Statement importer = null;
		for (Statement i : STATEMENT_IMPORTERS) {
			if (i.getDisplayName().equals(importerName)) {
				importer = i;
			}
		}
		
		return importer;
	}
}
