# Booking Platform Requirements

## 1. Project Overview

The Booking Platform is a reactive Spring Boot application for managing catalog items and user services. It provides a foundation for building a scalable booking system with reactive programming principles.

### 1.1 Tech Stack

- **Java 21**: Core programming language
- **Spring Boot 3.5.4**: Application framework
- **Spring WebFlux**: Reactive web framework
- **Spring Data R2DBC**: Reactive database access
- **PostgreSQL**: Database system
- **Liquibase**: Database migration tool
- **Lombok**: Reduces boilerplate code
- **Gradle**: Build and dependency management
- **MapStruct**: Bean mapping framework

## 2. Current Implementation Status

The project currently has:
- A well-defined `Catalog` domain model as a Java record with proper validation
- Enums for `AvailabilityStatus` and `ComplianceStatus`
- A `CatalogDTO` with Jakarta Validation annotations
- A `CatalogMapper` interface using MapStruct
- A `CatalogRepository` interface extending `ReactiveCrudRepository` with various query methods
- Minimal application configuration in `application.yml`
- Basic application structure with `UserServiceApplication` as the main class

## 3. Required Improvements

### 3.1 Application Configuration

#### 3.1.1 Expand application.yml

The `application.yml` file should be expanded with proper configuration:

```yaml
spring:
  application:
    name: booking-platform
  r2dbc:
    url: r2dbc:postgresql://${DB_HOST}/booking_platform
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
  liquibase:
    url: jdbc:postgresql://localhost:5432/booking_platform
    user: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    change-log: classpath:db/changelog/db.changelog-master.xml
  jpa:
    open-in-view: false

server:
  port: 8080
  error:
    include-message: always
    include-binding-errors: always

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when_authorized
```

#### 3.1.2 Implement Typed Properties

Create a `@ConfigurationProperties` class to centralize configuration:

```java
@ConfigurationProperties(prefix = "booking.platform")
@Validated
public record BookingPlatformProperties(
    @NotNull @Valid DatabaseProperties database,
    @NotNull @Valid SecurityProperties security
) {
    public record DatabaseProperties(
        @NotBlank String schema,
        @Min(1) @Max(100) int maxPoolSize
    ) {}
    
    public record SecurityProperties(
        @NotBlank String apiKey,
        @Min(60) @Max(3600) int tokenExpirySeconds
    ) {}
}
```

### 3.2 Service Layer Implementation

Create a service layer with clear transaction boundaries:

```java
@Service
public class CatalogService {
    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;
    
    public CatalogService(CatalogRepository catalogRepository, CatalogMapper catalogMapper) {
        this.catalogRepository = catalogRepository;
        this.catalogMapper = catalogMapper;
    }
    
    @Transactional(readOnly = true)
    public Flux<CatalogDTO> findAll() {
        return catalogRepository.findAll()
            .map(catalogMapper::toDto);
    }
    
    @Transactional(readOnly = true)
    public Mono<CatalogDTO> findById(UUID id) {
        return catalogRepository.findById(id)
            .map(catalogMapper::toDto);
    }
    
    @Transactional
    public Mono<CatalogDTO> create(CatalogDTO catalogDTO) {
        return Mono.just(catalogDTO)
            .map(catalogMapper::toEntity)
            .flatMap(catalogRepository::save)
            .map(catalogMapper::toDto);
    }
    
    @Transactional
    public Mono<CatalogDTO> update(UUID id, CatalogDTO catalogDTO) {
        return catalogRepository.findById(id)
            .map(catalog -> catalogMapper.updateEntityFromDto(catalogDTO, catalog))
            .flatMap(catalogRepository::save)
            .map(catalogMapper::toDto);
    }
    
    @Transactional
    public Mono<Void> delete(UUID id) {
        return catalogRepository.deleteById(id);
    }
}
```

### 3.3 Web Layer Implementation

Create a RESTful controller following REST API design principles:

```java
@RestController
@RequestMapping("/api/v1/catalogs")
class CatalogController {
    private final CatalogService catalogService;
    
    CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
    @GetMapping
    Flux<CatalogDTO> getAllCatalogs() {
        return catalogService.findAll();
    }
    
    @GetMapping("/{id}")
    Mono<ResponseEntity<CatalogDTO>> getCatalogById(@PathVariable UUID id) {
        return catalogService.findById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    Mono<ResponseEntity<CatalogDTO>> createCatalog(@Valid @RequestBody CatalogDTO catalogDTO) {
        return catalogService.create(catalogDTO)
            .map(created -> ResponseEntity
                .created(URI.create("/api/v1/catalogs/" + created.id()))
                .body(created));
    }
    
    @PutMapping("/{id}")
    Mono<ResponseEntity<CatalogDTO>> updateCatalog(
            @PathVariable UUID id, 
            @Valid @RequestBody CatalogDTO catalogDTO) {
        return catalogService.update(id, catalogDTO)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    Mono<ResponseEntity<Void>> deleteCatalog(@PathVariable UUID id) {
        return catalogService.delete(id)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
```

### 3.4 Exception Handling

Implement centralized exception handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleValidationException(WebExchangeBindException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Validation Error");
        
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> error.getDefaultMessage() == null ? "Invalid value" : error.getDefaultMessage(),
                (existing, replacement) -> existing + "; " + replacement
            ));
        
        problem.setProperty("errors", errors);
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem));
    }
    
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetail>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problem.setTitle("Server Error");
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem));
    }
}
```

### 3.5 Internationalization

Implement ResourceBundles for internationalization:

1. Create `src/main/resources/messages.properties`:
```properties
catalog.validation.title.notblank=Title is required
catalog.validation.title.size=Title must be between {min} and {max} characters
catalog.validation.description.notblank=Description is required
catalog.validation.description.size=Description must be between {min} and {max} characters
# Add more messages
```

2. Configure message source:
```java
@Configuration
public class MessageConfig {
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    
    @Bean
    public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
```

### 3.6 Testing Improvements

Implement integration tests with Testcontainers:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CatalogIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> 
            String.format("r2dbc:postgresql://%s:%d/%s", 
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName()));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Test
    void shouldCreateAndRetrieveCatalog() {
        // Test implementation
    }
}
```

### 3.7 Logging Configuration

Implement proper logging configuration:

Create `src/main/resources/logback-spring.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
    
    <logger name="com.jade.platform" level="DEBUG"/>
    <logger name="org.springframework.data.r2dbc" level="INFO"/>
</configuration>
```

### 3.8 Additional Improvements

#### 3.8.1 Package-private Visibility

Use package-private visibility for controllers, configuration classes, and bean methods when possible to improve encapsulation.

#### 3.8.2 Constructor Injection

Continue using constructor injection for dependencies as already implemented in the project.

#### 3.8.3 Disable OSIV

Ensure `spring.jpa.open-in-view: false` is set in application.yml (already included in the configuration above).

#### 3.8.4 Pagination

Implement pagination for collection endpoints:

```java
@GetMapping
Flux<CatalogDTO> getAllCatalogs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    return catalogService.findAll(PageRequest.of(page, size));
}
```

#### 3.8.5 API Documentation

Add Springdoc OpenAPI for API documentation:

1. Add dependency:
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0'
```

2. Configure OpenAPI:
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info().title("Booking Platform API").version("1.0.0"))
                .servers(List.of(new Server().url("/api/v1")));
    }
}
```

3. Configure in application.yml:
```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
    operationsSorter: method
    tagsSorter: alpha
    displayOperationId: true
    showExtensions: true
    showCommonExtensions: true
```

## 4. Implementation Priorities

1. **High Priority**:
   - Expand application configuration
   - Implement service layer
   - Implement web layer (controllers)
   - Add centralized exception handling

2. **Medium Priority**:
   - Implement internationalization
   - Configure proper logging
   - Add pagination support
   - Implement API documentation

3. **Low Priority**:
   - Implement integration tests with Testcontainers
   - Refine package-private visibility

## 5. Conclusion

The Booking Platform project has a solid foundation with well-designed domain models, DTOs, and repositories. By implementing the recommended improvements, the project will better align with Spring Boot best practices, enhance maintainability, improve error handling, and provide better support for internationalization and testing.

Key focus areas should be:
1. Expanding configuration with typed properties
2. Implementing proper service and controller layers
3. Adding centralized exception handling
4. Setting up comprehensive testing with Testcontainers
5. Configuring proper logging and internationalization

These improvements will result in a more robust, maintainable, and production-ready application.