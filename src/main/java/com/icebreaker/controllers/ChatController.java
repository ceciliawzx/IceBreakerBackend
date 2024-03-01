package com.icebreaker.controllers;

import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.ChatService;
import com.icebreaker.websocket.ChatMessage;
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
    private final ServerRunner serverRunner = ServerRunner.getInstance();

    @MessageMapping("/room/{roomCode}/sendMessage")
    public void handleMessage(@Payload ChatMessage message) {
        // Handle the message
        // Draw & Guess: handle guess messages
        // When the RoomStatus changes to PICTURING, any message sent to server will be regarded as a guess
        String roomCode = message.getRoomCode();
        if (serverRunner.getStatus(roomCode) == RoomStatus.PICTURING) {
            chatService.checkGuessWord(roomCode, message);
        } else {
            // Otherwise, don't do anything to the message
            System.out.println("handleMessage has been triggered, received message: " + message);
            message.setContent("Server has received your message: " + message.getContent());
        }
        chatService.broadcastToRoom(roomCode, message);
    }

    @PostMapping(path = "/guessedCorrect", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void guessedCorrect(@RequestParam(name = "roomCode") String roomCode, @RequestBody ChatMessage message) {
        System.out.println("GuessedCorrect, receives message " + message + "in room " + roomCode);
        chatService.addCorrectGuesser(roomCode, message);
        chatService.broadcastToRoom(roomCode, message);
    }
}
