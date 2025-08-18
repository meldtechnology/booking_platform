package com.jade.platform.config;

import com.jade.platform.domain.model.Catalog;
import com.jade.platform.domain.repository.CatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Minimal test for the DataInitializer component.
 * This test verifies that sample data is created when the application starts.
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerMinimalTest {

    @Mock
    private CatalogRepository catalogRepository;
    
    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * Test that verifies 50 sample catalog items are created.
     */
    @Test
    void shouldCreateSampleData() throws Exception {
        // Mock the count method to return 0 (empty database)
        when(catalogRepository.count()).thenReturn(Mono.just(0L));
        
        // Mock the save method to return the same catalog that was passed in
        when(catalogRepository.save(any(Catalog.class))).thenAnswer(invocation -> {
            Catalog catalog = invocation.getArgument(0);
            return Mono.just(catalog);
        });
        
        // Create the DataInitializer with mocked dependencies
        DataInitializer dataInitializer = new DataInitializer(catalogRepository, r2dbcEntityTemplate);
        
        // Execute the initializeData method and subscribe to the result
        StepVerifier.create(dataInitializer.initializeData())
            .verifyComplete();
        
        // Verify that save was called 50 times (for each sample catalog item)
        verify(catalogRepository, times(50)).save(any(Catalog.class));
    }
}