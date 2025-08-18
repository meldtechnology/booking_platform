package com.jade.platform.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralized exception handling for the application.
 * Uses ProblemDetail for standardized error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final MessageSource messageSource;
    
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    /**
     * Handle validation exceptions.
     * 
     * @param ex the WebExchangeBindException
     * @return the ResponseEntity with status 400 (Bad Request) and validation errors
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleValidationException(WebExchangeBindException ex) {
        log.debug("Handling validation exception: {}", ex.getMessage());
        
        Locale locale = LocaleContextHolder.getLocale();
        String detail = messageSource.getMessage("error.validation", null, locale);
        String title = messageSource.getMessage("error.validation.title", null, locale);
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle(title);
        
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                error -> error.getDefaultMessage() == null ? 
                    messageSource.getMessage("error.validation.default", null, "Invalid value", locale) : 
                    error.getDefaultMessage(),
                (existing, replacement) -> existing + "; " + replacement
            ));
        
        problem.setProperty("errors", errors);
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem));
    }
    
    /**
     * Handle resource not found exceptions.
     * 
     * @param ex the ResourceNotFoundException
     * @return the ResponseEntity with status 404 (Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.debug("Handling resource not found exception: {}", ex.getMessage());
        
        Locale locale = LocaleContextHolder.getLocale();
        String title = messageSource.getMessage("error.resource.notfound.title", null, locale);
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle(title);
        
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem));
    }
    
    /**
     * Handle business logic exceptions.
     * 
     * @param ex the BusinessException
     * @return the ResponseEntity with status 400 (Bad Request)
     */
    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleBusinessException(BusinessException ex) {
        log.debug("Handling business exception: {}", ex.getMessage());
        
        Locale locale = LocaleContextHolder.getLocale();
        String title = messageSource.getMessage("error.business.title", null, locale);
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(title);
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem));
    }
    
    /**
     * Handle all other exceptions.
     * 
     * @param ex the Exception
     * @return the ResponseEntity with status 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ProblemDetail>> handleGenericException(Exception ex) {
        log.error("Unhandled exception", ex);
        
        Locale locale = LocaleContextHolder.getLocale();
        String detail = messageSource.getMessage("error.server", null, locale);
        String title = messageSource.getMessage("error.server.title", null, locale);
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, detail);
        problem.setTitle(title);
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem));
    }
}