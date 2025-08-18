package com.jade.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Booking Platform application.
 * <p>
 * This Spring Boot application provides a reactive API for managing catalog items
 * and user services. It serves as a foundation for building a scalable booking system
 * using reactive programming principles.
 * </p>
 * 
 * @author Jade Platform Team
 * @version 1.0.0
 */
@SpringBootApplication
public class UserServiceApplication {

	/**
	 * The main method that starts the Spring Boot application.
	 * 
	 * @param args command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
