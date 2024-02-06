package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.ChatService;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WaitRoomHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/backToWaitRoom")
    public String backToWaitRoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        System.out.println("Back to wait room: " + roomCode);
        if (runner.changeRoomStatus(roomCode, RoomStatus.WAITING)) {
            runner.setTargetInRoom(roomCode, "");
            runner.addToPresentedList(roomCode);
            return "Success";
        }
        return "Fail";
    }
}