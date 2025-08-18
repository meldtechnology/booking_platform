package com.jade.platform.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Configuration for message source and validation messages.
 * <p>
 * This configuration class sets up internationalization (i18n) support for the application
 * by configuring a {@link MessageSource} bean that loads messages from properties files.
 * It also configures the validator to use these messages for validation error responses.
 * </p>
 * <p>
 * The application uses ResourceBundles for internationalization with the following files:
 * <ul>
 *   <li>messages.properties - Default locale messages</li>
 *   <li>messages_XX.properties - Locale-specific messages (where XX is the language code)</li>
 * </ul>
 * </p>
 * <p>
 * This configuration follows Spring Boot best practices by using package-private visibility
 * and clear bean definitions.
 * </p>
 *
 * @author Jade Platform Team
 * @version 1.0.0
 */
@Configuration
class MessageConfig {
    
    /**
     * Configure the message source for internationalization.
     * <p>
     * Creates and configures a {@link ReloadableResourceBundleMessageSource} that loads
     * message properties from classpath resources. The message source is configured to:
     * <ul>
     *   <li>Load messages from "classpath:messages" resource bundles</li>
     *   <li>Use UTF-8 encoding for proper character support</li>
     * </ul>
     * </p>
     * 
     * @return the configured message source
     */
    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
    
    /**
     * Configure the validator to use the message source for validation messages.
     * <p>
     * This bean configures the Jakarta Bean Validation implementation to use
     * the application's message source for validation error messages. This allows
     * validation error messages to be internationalized using the same message
     * properties as the rest of the application.
     * </p>
     * 
     * @param messageSource the message source to use for validation messages
     * @return the configured validator factory bean
     */
    @Bean
    LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}