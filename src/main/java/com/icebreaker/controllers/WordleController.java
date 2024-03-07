package com.icebreaker.controllers;

import com.icebreaker.dto.websocket.WordleMessage;
import com.icebreaker.services.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class WordleController {

    private final WordleService wordleService;

    @Autowired
    public WordleController(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    /* WebSocket */
    @MessageMapping("/room/{roomCode}/wordle")
    public void handleMessage(@Payload WordleMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        wordleService.broadcastResult(roomCode, message);
    }

    /* HTTP Handler */
    @PostMapping("/startWordle")
    public boolean startWordle(@RequestParam(name = "roomCode") String roomCode,
                               @RequestParam(name = "userID") String userID,
                               @RequestParam(name = "field") String field) {
        return wordleService.startWordle(roomCode, userID, field);
    }

    @GetMapping("/getWordleInfo")
    public int getWordleInfo(@RequestParam(name = "roomCode") String roomCode) {
        return wordleService.getWordleInfo(roomCode);
    }

    @GetMapping("/getWordleAnswer")
    public String getWordleAnswer(@RequestParam(name = "roomCode") String roomCode) {
        return wordleService.getWordleAnswer(roomCode);
    }

    @GetMapping("/getWordleGameStatus")
    public String getWordleGameStatus(@RequestParam(name = "roomCode") String roomCode) {
        return wordleService.getWordleGameStatus(roomCode);
    }

}
