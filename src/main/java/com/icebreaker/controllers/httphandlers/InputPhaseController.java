package com.icebreaker.controllers.httphandlers;

import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InputPhaseController {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    public InputPhaseController(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("/startPresenting")
    public boolean startPresenting(@RequestParam(name = "roomCode") String roomCode) {
        boolean result = runner.startPresenting(roomCode);
        if (result) {
            waitRoomService.broadcastMessage(roomCode);
        }
        return result;
    }

    @GetMapping("/infoComplete")
    public boolean checkPlayerInfoComplete(@RequestParam(name = "roomCode") String roomCode,
                                           @RequestParam(name = "userID") String userID) {
        return runner.checkPlayerInfoComplete(roomCode, userID);
    }
}
