package com.jade.platform.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Typed configuration properties for the Booking Platform application.
 * <p>
 * This class centralizes application-specific configuration properties with the common prefix
 * "booking.platform". It uses Jakarta Validation annotations to ensure required properties
 * are provided and valid, causing the application to fail fast if the configuration is invalid.
 * </p>
 * <p>
 * The properties are organized into nested records for different configuration areas:
 * <ul>
 *   <li>{@link DatabaseProperties} - Database-related configuration</li>
 *   <li>{@link SecurityProperties} - Security-related configuration</li>
 * </ul>
 * </p>
 * <p>
 * Example configuration in application.yml:
 * <pre>
 * booking:
 *   platform:
 *     database:
 *       schema: booking
 *       max-pool-size: 10
 *     security:
 *       api-key: your-api-key
 *       token-expiry-seconds: 3600
 * </pre>
 * </p>
 * 
 * @author Jade Platform Team
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "booking.platform")
@Validated
public record BookingPlatformProperties(
    @NotNull @Valid DatabaseProperties database,
    @NotNull @Valid SecurityProperties security
) {
    /**
     * Database-specific configuration properties.
     * <p>
     * Contains settings related to database connections and schema.
     * </p>
     */
    public record DatabaseProperties(
        /**
         * The database schema name.
         * Must not be blank.
         */
        @NotBlank String schema,
        
        /**
         * The maximum connection pool size.
         * Must be between 1 and 100.
         */
        @Min(1) @Max(100) int maxPoolSize
    ) {}
    
    /**
     * Security-specific configuration properties.
     * <p>
     * Contains settings related to API security and authentication.
     * </p>
     */
    public record SecurityProperties(
        /**
         * The API key used for authentication.
         * Must not be blank.
         */
        @NotBlank String apiKey,
        
        /**
         * The token expiry time in seconds.
         * Must be between 60 seconds (1 minute) and 3600 seconds (1 hour).
         */
        @Min(60) @Max(3600) int tokenExpirySeconds
    ) {}
}