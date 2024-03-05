package com.icebreaker.controllers.httphandlers;

import org.springframework.web.bind.annotation.*;

@RestController
public class HttpRequestsController {

    @GetMapping("/myEndpoint")
    public String handleRequest(@RequestParam(name = "message", required = false) String message) {
        System.out.println("My endPoint " + message);
        return "Received message: " + message;
    }
}
