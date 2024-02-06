package com.icebreaker.controllers.httphandlers;

import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InputPhaseHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/startInput")
    public String startInput(@RequestParam(name = "roomCode", required = true) String roomCode) {
        System.out.printf("Start Room: %s%n", roomCode);
        if (runner.serverStartRoom(roomCode)) {
            return "Success";
        }
        return "Fail";
    }

    @GetMapping("/infoComplete")
    public boolean checkPlayerInfoComplete(@RequestParam(name = "roomCode", required = true) String roomCode,
                                           @RequestParam(name = "userID", required = true) String userID) {
        System.out.printf("Info Complete, User: %s, Room: %s%n", userID, roomCode);
        return runner.checkPlayerInfoComplete(roomCode, userID);
    }
}
