package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
import jdk.jshell.execution.Util;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class DrawAndGuessHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/startDrawAndGuess")
    public String startDrawAndGuess(@RequestParam(name = "roomCode", required = true) String roomCode,
                                    @RequestParam(name = "fieldName", required = true) String fieldName,
                                    @RequestParam(name = "targetWord", required = true) String targetWord) {
        System.out.println("Start Draw and Guess in room: " + roomCode + " with targetWord: " + targetWord);
        if (runner.changeRoomStatus(roomCode, RoomStatus.PICTURING)) {
            runner.setTargetInRoom(roomCode, new Target(fieldName, targetWord));
            return "Success";
        }
        return "Fail";
    }

    @GetMapping("/getTarget")
    public String getTarget(@RequestParam(name = "roomCode", required = true) String roomCode) {
        System.out.println("Get target in room: " + roomCode);
        Room room = runner.getRoom(roomCode);
        System.out.println("getTarget: " + room.getTarget());
        Target target = room.getTarget();
        return JsonUtils.returnJson("target", target);
    }
}
