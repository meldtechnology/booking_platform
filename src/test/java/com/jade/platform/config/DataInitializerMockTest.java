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
 * Unit test for the DataInitializer using mocks.
 * This test verifies the behavior of the DataInitializer without requiring a database.
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerMockTest {

    @Mock
    private CatalogRepository catalogRepository;
    
    @Mock
    private R2dbcEntityTemplate template;

    @Test
    void shouldCreateSampleDataWhenDatabaseIsEmpty() throws Exception {
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
    
    @Test
    void shouldNotCreateSampleDataWhenDatabaseHasData() throws Exception {
        // Mock the count method to return a non-zero value (database already has data)
        when(catalogRepository.count()).thenReturn(Mono.just(10L));
        
        // Create the DataInitializer with mocked dependencies
        DataInitializer dataInitializer = new DataInitializer(catalogRepository, template);
        
        // Execute the initializeData method and subscribe to the result
        dataInitializer.initializeData().block();
        
        // Verify that save was never called (no data should be created)
        verify(catalogRepository, never()).save(any(Catalog.class));
    }
}