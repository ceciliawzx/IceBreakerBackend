package com.icebreaker.controllers;

import com.icebreaker.websocket.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {


    @MessageMapping("/room/{roomNumber}/sendMessage")
    @SendTo("/topic/room/{roomNumber}")
    public ChatMessage handleMessage(@DestinationVariable("roomNumber") int roomNumber, @Payload ChatMessage message) {
        System.out.println("handleMessage has been triggered, received message: " + message.toString());
        message.setContent("Server has received your message: " + message.getContent());
        return message;
    }

    @MessageMapping("/app/connect")
    public void connect(Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        // Get the user identifier (e.g., user ID) from the principal
        String userId = principal.getName();

        System.out.println("ID: " + userId);
    }
}
