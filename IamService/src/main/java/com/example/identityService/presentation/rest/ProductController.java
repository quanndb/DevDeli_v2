package com.example.identityService.presentation.rest;

import com.example.identityService.application.DTO.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    @PreAuthorize("hasPermission(null, 'products.read')")
    public ApiResponse<String> getProducts(){
        return ApiResponse.<String>builder()
                .code(200)
                .message("ok")
                .build();
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'products.create')")
    public ApiResponse<String> create(){
        return ApiResponse.<String>builder()
                .code(200)
                .message("ok")
                .build();
    }
}
