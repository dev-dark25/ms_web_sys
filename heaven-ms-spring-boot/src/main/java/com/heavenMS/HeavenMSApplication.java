package com.heavenMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class HeavenMSApplication {

    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(HeavenMSApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(HeavenMSApplication.class, args);
    }

}
