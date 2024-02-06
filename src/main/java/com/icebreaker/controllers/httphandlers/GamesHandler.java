package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.GameType;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GamesHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @GetMapping("/availableGames")
    public List<GameType> availableGames(@RequestParam(name = "roomCode", required = true) String roomCode,
                                         @RequestParam(name = "userID", required = true) String userID,
                                         @RequestParam(name = "fieldName", required = true) String fieldName) {
        System.out.println("Get available in room:" + roomCode + " User:" + userID + " Field:" + fieldName);
        return runner.availableGames(roomCode, userID, fieldName);
    }
}
