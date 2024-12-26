package com.devdeli.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway là điểm vào chính của hệ thống microservices
 * Nó đảm nhiệm các chức năng:
 * - Routing requests đến các service tương ứng
 * - Load balancing
 * - Circuit breaking để xử lý lỗi
 * - Request logging
 */
@SpringBootApplication  // Đánh dấu đây là ứng dụng Spring Boot
@EnableDiscoveryClient  // Kích hoạt tính năng service discovery với Eureka
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
