package com.example.nlpapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the Spring Boot application.
 * Handles the initialization and startup of the NLP Application.
 */
@SpringBootApplication
public class NlpAppApplication {

    // Logger instance for logging startup information
    private static final Logger logger = LoggerFactory.getLogger(NlpAppApplication.class);

    public static void main(String[] args) {
        // Launch the Spring Boot application
        ConfigurableApplicationContext context = SpringApplication.run(NlpAppApplication.class, args);

        // Log the startup event
        logger.info("NLP Application has started successfully.");

        // Optionally perform any initialization tasks here
    }
}
