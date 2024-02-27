package com.icebreaker.controllers.httphandlers;

import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InputPhaseHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;
    public InputPhaseHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("/startPresenting")
    public String startPresenting(@RequestParam(name = "roomCode") String roomCode) {
        System.out.printf("Start Room: %s%n", roomCode);
        if (runner.serverStartRoom(roomCode)) {
            waitRoomService.broadcastMessage(roomCode);
            return "Success";
        }
        return "Fail";
    }

    @GetMapping("/infoComplete")
    public boolean checkPlayerInfoComplete(@RequestParam(name = "roomCode") String roomCode,
                                           @RequestParam(name = "userID") String userID) {
        System.out.printf("Info Complete, User: %s, Room: %s%n", userID, roomCode);
        return runner.checkPlayerInfoComplete(roomCode, userID);
    }
}
