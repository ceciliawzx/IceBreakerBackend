package com.icebreaker.controllers;

import com.icebreaker.services.ChatService;
import com.icebreaker.services.DrawingService;
import com.icebreaker.services.WordleService;
import com.icebreaker.websocket.WordleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;


@Controller
public class WordleController {

    private final WordleService wordleService;

    @Autowired
    public WordleController(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @MessageMapping("/room/{roomNumber}/sendMessage")
    @SendTo("/topic/room/{roomNumber}")
    public void handleMessage(@Payload WordleMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        wordleService.checkCorrectness(roomCode, message);
        wordleService.broadcastResult(roomCode, message);
    }
}
