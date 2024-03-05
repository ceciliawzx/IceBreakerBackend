package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WaitRoomHandler {
    private final WaitRoomService waitRoomService;

    public WaitRoomHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("/backToWaitRoom")
    public boolean backToWaitRoom(@RequestParam(name = "roomCode") String roomCode) {
        return waitRoomService.backToPresentRoom(roomCode);
    }

    @PostMapping("/forceBackToAllPresentedRoom")
    public boolean forceBackToAllPresentedRoom(@RequestParam(name = "roomCode") String roomCode) {
        return waitRoomService.forceBackToAllPresentedRoom(roomCode);
    }
}
