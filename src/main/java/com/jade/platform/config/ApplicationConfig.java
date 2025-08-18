package com.jade.platform.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration class that enables the typed configuration properties.
 * <p>
 * This configuration class is responsible for enabling the {@link BookingPlatformProperties}
 * typed configuration properties class, which centralizes and validates application-specific
 * configuration properties with a common prefix.
 * </p>
 * <p>
 * The class follows Spring Boot best practices by using constructor injection
 * and package-private visibility to improve encapsulation.
 * </p>
 *
 * @see BookingPlatformProperties
 * @author Jade Platform Team
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(BookingPlatformProperties.class)
class ApplicationConfig {
    // Additional beans and configuration can be added here
}