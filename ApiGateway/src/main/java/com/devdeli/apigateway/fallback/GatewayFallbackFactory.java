package com.devdeli.apigateway.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.support.ServiceUnavailableException;

@Slf4j
@Component
public class GatewayFallbackFactory {

    public Mono<Void> fallback(ServerWebExchange exchange, Throwable throwable) {
        log.error("Fallback triggered due to: {}", throwable.getMessage());
        
        // Lấy thông tin lỗi gốc
        Throwable originalError = throwable.getCause() != null ? throwable.getCause() : throwable;
        
        // Chỉ fallback khi thực sự có lỗi kết nối
        if (isConnectionError(originalError)) {
            exchange.getAttributes().put(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, throwable);
            return Mono.error(new ServiceUnavailableException());
        }
        
        // Nếu không phải lỗi kết nối, để lỗi gốc được trả về
        return Mono.error(throwable);
    }

    private boolean isConnectionError(Throwable throwable) {
        return throwable instanceof java.io.IOException ||
                throwable instanceof java.util.concurrent.TimeoutException;
    }
} 