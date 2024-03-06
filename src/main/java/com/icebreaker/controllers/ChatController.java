package com.icebreaker.controllers;

import com.icebreaker.services.ChatService;
import com.icebreaker.dto.websocket.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RestController
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/room/{roomCode}/sendMessage")
    public void handleMessage(@Payload ChatMessage message) {
        chatService.handleMessage(message);
    }

    @PostMapping(path = "/guessedCorrect", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void guessedCorrect(@RequestParam(name = "roomCode") String roomCode, @RequestBody ChatMessage message) {
        chatService.addCorrectGuesser(roomCode, message);
        chatService.broadcastToRoom(roomCode, message);
    }
}
