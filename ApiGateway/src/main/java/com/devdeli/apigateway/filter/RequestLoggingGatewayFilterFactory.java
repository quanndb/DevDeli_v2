package com.devdeli.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Filter để log thông tin request/response
 * Giúp theo dõi và debug luồng request trong hệ thống
 */
@Component  // Đăng ký filter như một Spring bean
@Slf4j      // Lombok annotation để tạo logger
public class RequestLoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestLoggingGatewayFilterFactory.Config> {

    public RequestLoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Tạo một filter để log thông tin request và response
        return (exchange, chain) -> {
            // Lấy thông tin request
            String originalPath = exchange.getRequest().getPath().value();  // Path gốc của request
            String method = exchange.getRequest().getMethod().name();       // HTTP method (GET, POST,...)
            String targetUri = exchange.getRequest().getURI().toString();   // URI đích
            
            // Lấy thông tin route từ exchange
            Route route = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            String routeId = route.getId();

            // Log thông tin request
            log.info("Request: {} {} -> Route[{}] forwarding to: {}", 
                    method, originalPath, routeId, targetUri);

            // Xử lý request và log response
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        // Log thông tin response sau khi request được xử lý
                        int status = exchange.getResponse().getStatusCode().value();
                        log.info("Response: {} {} -> Route[{}] status: {}", 
                                method, originalPath, routeId, status);
                    }));
        };
    }

    /**
     * Class cấu hình cho filter
     * Hiện tại không cần thiết lập thêm properties
     */
    public static class Config {
        // Configuration properties if needed
    }
} 