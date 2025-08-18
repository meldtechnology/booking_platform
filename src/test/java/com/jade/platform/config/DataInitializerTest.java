package com.jade.platform.config;

import com.jade.platform.domain.model.Catalog;
import com.jade.platform.domain.repository.CatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for the DataInitializer component.
 * This test verifies that sample data is created when the application starts.
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private CatalogRepository catalogRepository;
    
    @Mock
    private R2dbcEntityTemplate template;

    /**
     * Test that verifies 50 sample catalog items are created when the database is empty.
     */
    @Test
    void shouldCreateSampleDataInDevProfile() throws Exception {
        // Mock the count method to return 0 (empty database)
        when(catalogRepository.count()).thenReturn(Mono.just(0L));
        
        // Mock the save method to return the same catalog that was passed in
        when(catalogRepository.save(any(Catalog.class))).thenAnswer(invocation -> {
            Catalog catalog = invocation.getArgument(0);
            return Mono.just(catalog);
        });
        
        // Create the DataInitializer with mocked dependencies
        DataInitializer dataInitializer = new DataInitializer(catalogRepository, template);
        
        // Execute the initializeData method and subscribe to the result
        dataInitializer.initializeData().block();
        
        // Verify that save was called 50 times (for each sample catalog item)
        verify(catalogRepository, times(50)).save(any(Catalog.class));
    }
}