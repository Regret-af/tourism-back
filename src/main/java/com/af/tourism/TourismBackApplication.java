package com.af.tourism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TourismBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourismBackApplication.class, args);
    }

}
