package com.devdeli.apigateway.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FallbackController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping("/fallback")
    public Mono<ResponseEntity<Object>> fallback(ServerWebExchange exchange) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("iamCircuitBreaker");
        
        // Lấy exception gốc
        Throwable exception = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
        
        log.error("Fallback triggered. State: {}, Error: {}", 
            circuitBreaker.getState(), errorMessage);

        return Mono.just(ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse(
                "Service Unavailable",
                String.format("Service is temporarily unavailable. State: %s, Error: %s",
                    circuitBreaker.getState(), errorMessage),
                503
            )));
    }
}

@Getter
@Slf4j
class ErrorResponse {
    // Getters
    private final String error;
    private final String message;
    private final int status;

    public ErrorResponse(String error, String message, int status) {
        this.error = error;
        this.message = message;
        this.status = status;
    }

}
