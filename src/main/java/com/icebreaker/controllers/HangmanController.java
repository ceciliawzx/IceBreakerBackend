package com.icebreaker.controllers;

import com.icebreaker.services.HangmanService;
import com.icebreaker.websocket.HangmanMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class HangmanController {

    private final HangmanService hangmanService;

    @Autowired
    public HangmanController(HangmanService hangmanService) {
        this.hangmanService = hangmanService;
    }

    @MessageMapping("/room/{roomCode}/hangman")
    public void handleMessage(@Payload HangmanMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        hangmanService.broadcastResult(roomCode, message);
    }
}
