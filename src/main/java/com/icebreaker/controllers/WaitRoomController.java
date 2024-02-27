package com.icebreaker.controllers;

import com.icebreaker.services.WaitRoomService;
import com.icebreaker.services.WordleService;
import com.icebreaker.websocket.WordleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WaitRoomController {
    private final WaitRoomService waitRoomService;

    @Autowired
    public WaitRoomController(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @MessageMapping("/room/{roomCode}/wait")
    public void handleMessage(@Payload String message) {
        System.out.println("Received: " + message);
        // String roomCode = String.valueOf(message.getRoomCode());
        // wordleService.broadcastResult(roomCode, message);
    }
}
