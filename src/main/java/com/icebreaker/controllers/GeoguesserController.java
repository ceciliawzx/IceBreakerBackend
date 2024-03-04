package com.icebreaker.controllers;

import com.icebreaker.services.GeoguesserService;
import com.icebreaker.websocket.GeoguesserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class GeoguesserController {
    private final GeoguesserService geoguesserService;

    @Autowired
    public GeoguesserController(GeoguesserService geoguesserService) {
        this.geoguesserService = geoguesserService;
    }

    @MessageMapping("/room/{roomCode}/sendGuessing")
    public void handleMessage(@Payload GeoguesserMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        System.out.println("Received: geoguessing" + message);
        geoguesserService.broadcastGuessing(roomCode, message);
    }
}
