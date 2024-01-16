package com.icebreaker.httprequests;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HttpRequests {

    private final AtomicInteger roomNumber = new AtomicInteger(0);

    @GetMapping("/myEndpoint")
    public String handleRequest(@RequestParam(name = "message", required = false) String message) {
        return "Received message: " + message;
    }

    @GetMapping("/createRoom")
    public String handleRoomCreation(@RequestParam(name = "message", required = false) String message) {
        int newRoomNumber = roomNumber.getAndIncrement();
        return "Room Created!!! Your New Room Number is " + newRoomNumber;
    }
}
