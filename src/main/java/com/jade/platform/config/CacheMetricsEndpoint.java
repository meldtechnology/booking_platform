package com.jade.platform.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom actuator endpoint for monitoring cache statistics.
 * <p>
 * This endpoint exposes detailed metrics for all Caffeine caches configured in the application.
 * It provides information such as hit rate, miss rate, eviction count, and size for each cache.
 * </p>
 * <p>
 * The endpoint is available at /actuator/cacheMetrics and requires no authentication.
 * </p>
 */
@Component
@Endpoint(id = "cacheMetrics")
public class CacheMetricsEndpoint {
    private static final Logger log = LoggerFactory.getLogger(CacheMetricsEndpoint.class);
    
    private final CacheManager cacheManager;
    
    /**
     * Creates a new CacheMetricsEndpoint with the given CacheManager.
     *
     * @param cacheManager the cache manager to retrieve cache statistics from
     */
    public CacheMetricsEndpoint(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    /**
     * Retrieves cache statistics for all caches.
     * <p>
     * This method is called when a GET request is made to /actuator/cacheMetrics.
     * It collects statistics for each cache and returns them as a map.
     * </p>
     *
     * @return a map containing cache statistics for all caches
     */
    @ReadOperation
    public Map<String, Object> cacheMetrics() {
        log.debug("Retrieving cache metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> caches = new HashMap<>();
        
        // Get all cache names from the cache manager
        cacheManager.getCacheNames().forEach(cacheName -> {
            CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(cacheName);
            if (caffeineCache != null) {
                Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
                
                // Get statistics for this cache
                Map<String, Object> cacheMetrics = new HashMap<>();
                cacheMetrics.put("size", nativeCache.estimatedSize());
                
                // Get cache stats
                CacheStats stats = nativeCache.stats();
                cacheMetrics.put("hitCount", stats.hitCount());
                cacheMetrics.put("missCount", stats.missCount());
                cacheMetrics.put("hitRate", stats.hitRate());
                cacheMetrics.put("missRate", stats.missRate());
                cacheMetrics.put("evictionCount", stats.evictionCount());
                cacheMetrics.put("loadSuccessCount", stats.loadSuccessCount());
                cacheMetrics.put("loadFailureCount", stats.loadFailureCount());
                cacheMetrics.put("totalLoadTime", stats.totalLoadTime());
                cacheMetrics.put("averageLoadPenalty", stats.averageLoadPenalty());
                
                caches.put(cacheName, cacheMetrics);
            }
        });
        
        metrics.put("caches", caches);
        return metrics;
    }
}