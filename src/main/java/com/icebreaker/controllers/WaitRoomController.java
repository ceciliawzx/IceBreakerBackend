package com.icebreaker.controllers;

import com.icebreaker.services.WaitRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WaitRoomController {

    @Autowired
    public WaitRoomController(WaitRoomService waitRoomService) {
    }

    @MessageMapping("/room/{roomCode}/wait")
    public void handleMessage(@Payload String message) {
        System.out.println("Received: " + message);
    }
}
