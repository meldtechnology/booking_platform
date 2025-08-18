package com.jade.platform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * WebFilter that adds request tracking information to the MDC (Mapped Diagnostic Context).
 */
@Component
class RequestLoggingFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    
    /**
     * Filters each web request to add request tracking information.
     *
     * @param exchange the current server exchange, providing access to the request and response
     * @param chain the filter chain to delegate to for the next filter
     * @return a Mono<Void> that completes when the request has been processed
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        
        final String finalRequestId = requestId;
        exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, finalRequestId);
        
        return chain.filter(exchange)
            .contextWrite(context -> context.put(REQUEST_ID_MDC_KEY, finalRequestId))
            .doOnSubscribe(subscription -> {
                MDC.put(REQUEST_ID_MDC_KEY, finalRequestId);
                log.debug("Request started: {} {}", 
                    exchange.getRequest().getMethod(), 
                    exchange.getRequest().getURI());
            })
            .doFinally(signalType -> {
                log.debug("Request completed with status: {}", 
                    exchange.getResponse().getStatusCode());
                MDC.remove(REQUEST_ID_MDC_KEY);
            });
    }
}