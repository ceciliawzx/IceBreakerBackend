package com.icebreaker.controllers;

import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.ChatService;
import com.icebreaker.websocket.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
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
            Room room = serverRunner.getRoom(roomCode);
            String target = room.getTarget();
            // If the guess is correct, only broadcast who has guessed correct
            if (target.equalsIgnoreCase(message.getContent())) {
                message.setContent(message.getSender() + " has guessed right!");
                message.setSender("System");
                // TODO: Somehow notify the frontend to change the display ig?
            }
        } else {
            // Otherwise, don't do anything to the message
            System.out.println("handleMessage has been triggered, received message: " + message);
            message.setContent("Server has received your message: " + message.getContent());
        }
        chatService.broadcastToRoom(roomCode, message);
    }
}
