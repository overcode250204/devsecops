package com.example.devsecopslab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class DevsecopsLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevsecopsLabApplication.class, args);
    }
}
