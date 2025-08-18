package com.jade.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for OpenAPI documentation.
 */
@Configuration
class OpenApiConfig {
    
    /**
     * Configures the OpenAPI documentation.
     *
     * @return the OpenAPI configuration bean
     */
    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking Platform API")
                        .description("API for managing catalog items in the Booking Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Jade Platform Team")
                                .email("support@jade-platform.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("/").description("Default Server URL")
                ));
    }
}