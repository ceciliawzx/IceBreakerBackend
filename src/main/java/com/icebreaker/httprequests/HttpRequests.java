package com.icebreaker.httprequests;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpRequests {

    @GetMapping("/myEndpoint")
    public String handleRequest(@RequestParam(name = "message", required = false) String message) {
        return "Received message: " + message;
    }

    @GetMapping("/createRoom")
    public String handleRoomCreation(@RequestParam(name = "message", required = false) String message) {
        return "Room Created!!! OMG I am soooooooooooooooooooooooooo SmartttttttttttttT!!!!!!!!!!!!!!!!!!";
    }
}
