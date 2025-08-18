package com.jade.platform.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.PoolMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Custom actuator endpoint for monitoring R2DBC connection pool metrics.
 * <p>
 * This endpoint exposes detailed metrics for the R2DBC connection pool, including
 * allocated connections, idle connections, and pending connections.
 * </p>
 * <p>
 * The endpoint is available at /actuator/connectionPoolMetrics and requires no authentication.
 * </p>
 */
@Component
@Endpoint(id = "connectionPoolMetrics")
public class ConnectionPoolMetricsEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolMetricsEndpoint.class);
    
    private final ConnectionPool connectionPool;
    
    /**
     * Creates a new ConnectionPoolMetricsEndpoint with the given ConnectionPool.
     *
     * @param connectionPool the connection pool to retrieve metrics from
     */
    public ConnectionPoolMetricsEndpoint(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        log.info("Connection pool metrics endpoint initialized");
    }
    
    /**
     * Retrieves connection pool metrics.
     * <p>
     * This method is called when a GET request is made to /actuator/connectionPoolMetrics.
     * It collects metrics from the connection pool and returns them as a map.
     * </p>
     *
     * @return a map containing connection pool metrics
     */
    @ReadOperation
    public Map<String, Object> connectionPoolMetrics() {
        log.debug("Retrieving connection pool metrics");
        
        Map<String, Object> metrics = new HashMap<>();
        Optional<PoolMetrics> poolMetricsOptional = connectionPool.getMetrics();
        
        if (poolMetricsOptional.isPresent()) {
            PoolMetrics poolMetrics = poolMetricsOptional.get();
            
            metrics.put("acquiredSize", poolMetrics.acquiredSize());
            metrics.put("allocatedSize", poolMetrics.allocatedSize());
            metrics.put("idleSize", poolMetrics.idleSize());
            metrics.put("pendingAcquireSize", poolMetrics.pendingAcquireSize());
            
            // Add pool configuration information
            metrics.put("maxSize", getPoolMaxSize());
            metrics.put("initialSize", getPoolInitialSize());
        } else {
            metrics.put("error", "Connection pool metrics not available");
        }
        
        return metrics;
    }
    
    /**
     * Gets the maximum size of the connection pool from the application configuration.
     * 
     * @return the maximum size of the connection pool
     */
    private int getPoolMaxSize() {
        // This is a hardcoded value for now, but in a real application,
        // you would get this from the application configuration
        return 10;
    }
    
    /**
     * Gets the initial size of the connection pool from the application configuration.
     * 
     * @return the initial size of the connection pool
     */
    private int getPoolInitialSize() {
        // This is a hardcoded value for now, but in a real application,
        // you would get this from the application configuration
        return 5;
    }
}