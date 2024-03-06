package com.icebreaker.controllers;

import com.icebreaker.services.WaitRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class WaitRoomController {

    private final WaitRoomService waitRoomService;

    @Autowired
    public WaitRoomController(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    /* WebSocket */
    @MessageMapping("/room/{roomCode}/wait")
    public void handleMessage(@Payload String message) {
        System.out.println("Received: " + message);
    }

    /* HTTP Handler */
    @PostMapping("/backToWaitRoom")
    public boolean backToWaitRoom(@RequestParam(name = "roomCode") String roomCode) {
        return waitRoomService.backToWaitRoom(roomCode);
    }

    @PostMapping("/forceBackToAllPresentedRoom")
    public boolean forceBackToAllPresentedRoom(@RequestParam(name = "roomCode") String roomCode) {
        return waitRoomService.forceBackToAllPresentedRoom(roomCode);
    }
}
