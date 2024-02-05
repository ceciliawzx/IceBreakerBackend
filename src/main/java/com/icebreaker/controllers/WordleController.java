package com.icebreaker.controllers;

import com.icebreaker.services.WordleService;
import com.icebreaker.websocket.WordleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WordleController {

    private final WordleService wordleService;

    @Autowired
    public WordleController(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @MessageMapping("/room/{roomCode}/wordle")
    public void handleMessage(@Payload WordleMessage message) {
        System.out.println("Received: " + message);
        String roomCode = String.valueOf(message.getRoomCode());
        wordleService.broadcastResult(roomCode, message);
    }
}
