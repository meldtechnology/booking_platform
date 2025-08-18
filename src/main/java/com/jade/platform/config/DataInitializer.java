package com.jade.platform.config;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import com.jade.platform.domain.model.Catalog;
import com.jade.platform.domain.repository.CatalogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Configuration class responsible for initializing sample data in the development environment.
 * This class will create 50 sample catalog items when the application starts in the dev profile.
 */
@Configuration
@Profile({"dev", "test"})
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final int SAMPLE_DATA_COUNT = 50;
    private static final Random random = new Random();
    
    private final CatalogRepository catalogRepository;
    private final R2dbcEntityTemplate template;
    
    public DataInitializer(CatalogRepository catalogRepository, R2dbcEntityTemplate template) {
        this.catalogRepository = catalogRepository;
        this.template = template;
    }

    /**
     * Creates a CommandLineRunner bean that initializes sample data for the dev environment.
     * 
     * @return A CommandLineRunner that initializes sample data
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            initializeData().block();
        };
    }
    
    /**
     * Initializes sample data for testing and development.
     * This method is extracted to make it testable without blocking.
     * 
     * @return A Mono that completes when initialization is done
     */
    public Mono<Void> initializeData() {
        logger.info("Starting sample data initialization...");
        
        return catalogRepository.count()
            .flatMap(count -> {
                if (count > 0) {
                    logger.info("Database already contains {} catalog items, skipping initialization", count);
                    return Mono.empty();
                }
                
                logger.info("Generating {} sample catalog items", SAMPLE_DATA_COUNT);
                
                // Generate sample catalog items
                List<Catalog> sampleCatalogs = IntStream.range(0, SAMPLE_DATA_COUNT)
                    .mapToObj(this::createSampleCatalog)
                    .toList();

                // Save all catalog items
                return Flux.fromIterable(sampleCatalogs)
                    .flatMap(catalogRepository::save)
                    .doOnNext(saved -> logger.debug("Saved catalog item: {}", saved.title()))
                    .then();
            })
            .doOnSuccess(v -> logger.info("Sample data initialization completed"))
            .onErrorResume(e -> {
                logger.error("Error initializing sample data", e);
                return Mono.error(new RuntimeException("Failed to initialize sample data", e));
            });
    }
    
    /**
     * Creates a sample catalog item with randomized data.
     * 
     * @param index The index of the sample item (used to create variety)
     * @return A new Catalog entity with sample data
     */
    private Catalog createSampleCatalog(int index) {
        // Create industry and merchant IDs
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        
        // Generate sample categories and tags
        List<String> categories = generateSampleCategories();
        List<String> tags = generateSampleTags();
        
        // Generate a random price between $10 and $1000
        BigDecimal price = BigDecimal.valueOf(10 + random.nextInt(991));
        
        // Generate a random rating between 1.0 and 5.0
        double rating = 1.0 + (random.nextDouble() * 4.0);
        
        // Randomly select compliance and availability status
        ComplianceStatus complianceStatus = random.nextBoolean() ? 
            ComplianceStatus.COMPLIANT : ComplianceStatus.NON_COMPLIANT;
            
        AvailabilityStatus availabilityStatus = random.nextBoolean() ? 
            AvailabilityStatus.AVAILABLE : AvailabilityStatus.UNAVAILABLE;
        
        // Current timestamp for created/updated dates
        Instant now = Instant.now();
        
        // Create and return the catalog item
        return Catalog.builder()
            // Let the database generate the ID
            .publicId(UUID.randomUUID())
            .title("Sample Catalog Item " + (index + 1))
            .description("This is a sample description for catalog item " + (index + 1) + 
                ". It contains detailed information about the product or service.")
            .industryId(industryId)
            .industryName(getRandomIndustryName())
            .categories(categories)
            .tags(tags)
            .price(price)
            .merchantId(merchantId)
            .rating(rating)
            .complianceStatus(complianceStatus)
            .availabilityStatus(availabilityStatus)
            .createdOn(now)
            .updatedOn(now)
            .build();
    }
    
    /**
     * Generates a random list of sample categories.
     * 
     * @return A list of sample categories
     */
    private List<String> generateSampleCategories() {
        List<String> allCategories = List.of(
            "Electronics", "Clothing", "Home & Garden", "Sports", "Beauty", 
            "Books", "Toys", "Automotive", "Health", "Food & Beverage"
        );
        
        // Select 1-3 random categories
        int count = 1 + random.nextInt(3);
        return IntStream.range(0, count)
            .mapToObj(i -> allCategories.get(random.nextInt(allCategories.size())))
            .distinct()
            .toList();
    }
    
    /**
     * Generates a random list of sample tags.
     * 
     * @return A list of sample tags
     */
    private List<String> generateSampleTags() {
        List<String> allTags = List.of(
            "new", "sale", "trending", "popular", "limited", 
            "exclusive", "featured", "premium", "budget", "eco-friendly"
        );
        
        // Select 2-5 random tags
        int count = 2 + random.nextInt(4);
        return IntStream.range(0, count)
            .mapToObj(i -> allTags.get(random.nextInt(allTags.size())))
            .distinct()
            .toList();
    }
    
    /**
     * Returns a random industry name.
     * 
     * @return A random industry name
     */
    private String getRandomIndustryName() {
        List<String> industries = List.of(
            "Technology", "Fashion", "Home Improvement", "Sports & Recreation", 
            "Health & Beauty", "Publishing", "Entertainment", "Automotive", 
            "Healthcare", "Food & Beverage", "Education", "Finance"
        );
        
        return industries.get(random.nextInt(industries.size()));
    }
}