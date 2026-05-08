package com.uju.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UjuPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(UjuPlatformApplication.class, args);
    }
}
