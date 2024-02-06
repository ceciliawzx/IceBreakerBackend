package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DrawAndGuessHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/startDrawAndGuess")
    public String startDrawAndGuess(@RequestParam(name = "roomCode", required = true) String roomCode,
                                    @RequestParam(name = "target", required = true) String target) {
        System.out.println("Start Draw and Guess in room: " + roomCode + " with target: " + target);
        if (runner.changeRoomStatus(roomCode, RoomStatus.PICTURING)) {
            runner.setTargetInRoom(roomCode, target);
            return "Success";
        }
        return "Fail";
    }

    @GetMapping("/getTarget")
    public String getTarget(@RequestParam(name = "roomCode", required = true) String roomCode) {
        System.out.println("Get target in room: " + roomCode);
        Room room = runner.getRoom(roomCode);
        System.out.println("getTarget: " + room.getTarget());
        return room.getTarget();
    }
}
