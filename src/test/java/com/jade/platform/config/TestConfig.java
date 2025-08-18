package com.jade.platform.config;

import com.jade.platform.domain.repository.CatalogRepository;
import io.r2dbc.spi.ConnectionFactory;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

/**
 * Test configuration class that provides necessary beans for tests.
 */
@TestConfiguration
public class TestConfig {

    /**
     * Creates an R2dbcEntityTemplate bean for tests.
     * 
     * @param connectionFactory the connection factory
     * @return an R2dbcEntityTemplate
     */
    @Bean
    @Primary
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
    
    /**
     * Creates a mock DataInitializer bean for tests.
     * 
     * @return a mock DataInitializer
     */
    @Bean
    @Primary
    public DataInitializer dataInitializer(CatalogRepository catalogRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        return new DataInitializer(catalogRepository, r2dbcEntityTemplate);
    }
}