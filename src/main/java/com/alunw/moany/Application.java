package com.alunw.moany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.support.DatabaseStartupValidator;

//import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.stream.Stream;

@SpringBootApplication
//@EnableJpaAuditing
//@EnableAutoConfiguration
//@Import({ SecurityConfig.class })
public class Application {

//    @Value("${moany.databaseStartupInterval:5}")
//    private int databaseStartupInterval;

//    @Value("${moany.databaseStartupTimeout:120}")
//    private int databaseStartupTimeout;

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            logger.debug("Command line runner placeholder.");
        };
    }

//    @Bean
//    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
//        DatabaseStartupValidator validator = new DatabaseStartupValidator();
//        validator.setDataSource(dataSource);
//        validator.setInterval(databaseStartupInterval);
//        validator.setTimeout(databaseStartupTimeout);
//        validator.setValidationQuery("SELECT 1");
//        return validator;
//    }

//    @Bean
//    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
//        return bf -> {
//            String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
//            Stream.of(jpa).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));
//        };
//    }
}
