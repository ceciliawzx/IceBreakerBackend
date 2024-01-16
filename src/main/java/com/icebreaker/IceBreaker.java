package com.icebreaker;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class IceBreaker {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(IceBreaker.class, args);
    }

    @GetMapping("/myEndpoint")
    public String handleRequest(@RequestParam(name = "message", required = false) String message) {
        System.out.println("YOYOYOYOYOYO");
        return "Received message: " + message;
    }
}
