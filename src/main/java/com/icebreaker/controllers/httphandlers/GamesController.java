package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.GameType;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GamesController {
    private final ServerRunner runner = ServerRunner.getInstance();

    @GetMapping("/availableGames")
    public List<GameType> availableGames(@RequestParam(name = "roomCode") String roomCode,
                                         @RequestParam(name = "userID") String userID,
                                         @RequestParam(name = "fieldName") String fieldName) {
        return runner.availableGames(roomCode, userID, fieldName);
    }
}
