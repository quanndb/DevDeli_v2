package com.example.storageservice.config.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class JacksonConfiguration {

    @Bean
    public SimpleModule localDateSerializer() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new CustomLocalDateSerializer());
        return module;
    }
}
