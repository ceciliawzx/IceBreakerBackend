package com.icebreaker.httprequests;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

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

    @GetMapping("/joinRoom")
    public String handleJoinRoom(@RequestParam(name = "roomNumber", required = true) int number,
                                 HttpServletRequest request) {
        return "You have joined room " + number + ". Your IP address is " + request.getRemoteAddr();
    }
}
