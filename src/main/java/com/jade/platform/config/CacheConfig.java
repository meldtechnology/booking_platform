package com.jade.platform.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for caching.
 * <p>
 * This class configures the caching infrastructure for the application using Caffeine as the
 * cache provider. It defines cache names, expiration policies, and maximum sizes for each cache.
 * </p>
 * <p>
 * The following caches are configured:
 * <ul>
 *   <li>catalogById: Caches catalog items by their ID with a 10-minute expiration</li>
 *   <li>catalogsByIndustry: Caches catalog items by industry ID with a 5-minute expiration</li>
 *   <li>allCatalogs: Caches the full catalog list with a 2-minute expiration</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);
    
    // Cache names as constants to avoid typos
    public static final String CACHE_CATALOG_BY_ID = "catalogById";
    public static final String CACHE_CATALOG_BY_PUBLIC_ID = "catalogByPublicId";
    public static final String CACHE_CATALOGS_BY_INDUSTRY = "catalogsByIndustry";
    public static final String CACHE_ALL_CATALOGS = "allCatalogs";
    
    /**
     * Creates and configures the cache manager.
     * <p>
     * This method creates a CaffeineCacheManager and configures it with the defined cache names.
     * It also sets up a default Caffeine cache specification with a 5-minute expiration and a
     * maximum size of 1000 entries.
     * </p>
     * 
     * @return the configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Configuring cache manager with Caffeine");
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // Enable async mode to support reactive types like Mono and Flux
        cacheManager.setAsyncCacheMode(true);

        // Set the cache names
        cacheManager.setCacheNames(Arrays.asList(
                CACHE_CATALOG_BY_ID,
                CACHE_CATALOG_BY_PUBLIC_ID,
                CACHE_CATALOGS_BY_INDUSTRY,
                CACHE_ALL_CATALOGS
        ));
        
        // Configure default cache specification
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(500)
                .recordStats());

        return cacheManager;
    }
}