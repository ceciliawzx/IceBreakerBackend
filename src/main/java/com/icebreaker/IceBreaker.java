package com.icebreaker;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class IceBreaker {

    public static void main(String[] args) {
        SpringApplication.run(IceBreaker.class, args);
    }
}
