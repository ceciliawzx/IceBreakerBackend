package com.icebreaker;

import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IceBreaker {

    public static void main(String[] args) {
        ServerRunner runner = ServerRunner.getInstance();
        SpringApplication.run(IceBreaker.class, args);
    }
}
