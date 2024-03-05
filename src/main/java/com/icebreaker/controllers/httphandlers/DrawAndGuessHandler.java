package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import com.icebreaker.utils.JsonUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DrawAndGuessHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    public DrawAndGuessHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("/startDrawAndGuess")
    public String startDrawAndGuess(@RequestParam(name = "roomCode") String roomCode,
                                    @RequestParam(name = "fieldName") String fieldName,
                                    @RequestParam(name = "targetWord") String targetWord) {
        System.out.println("Start Draw and Guess in room: " + roomCode + " with targetWord: " + targetWord);
        if (runner.changeRoomStatus(roomCode, RoomStatus.PICTURING)) {
            runner.setTargetInRoom(roomCode, new Target(fieldName, targetWord));
            waitRoomService.broadcastMessage(roomCode);
            return "Success";
        }
        return "Fail";
    }

    @GetMapping("/getTarget")
    public String getTarget(@RequestParam(name = "roomCode") String roomCode) {
        Room room = runner.getRoom(roomCode);
        Target target = room.getTarget();
        return JsonUtils.returnJson(Map.of("target", target), "Error fetching target of a room");
    }
}
