package com.icebreaker.controllers;

import com.icebreaker.websocket.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Objects;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/room/{roomNumber}/sendMessage")
    @SendTo("/topic/room/{roomNumber}")
    public ChatMessage handleMessage(SimpMessageHeaderAccessor headerAccessor, @Payload ChatMessage message) {
//        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
//        if (userId == null) {
//            throw new IllegalStateException("userId not found in session");
//        }
        System.out.println(headerAccessor);
        // Handle the message, knowing which user it came from
        System.out.println("handleMessage has been triggered, received message: " + message.toString());
        message.setContent("Server has received your message: " + message.getContent());
        return message;
    }

    public void broadcastToRoom(int roomNumber, ChatMessage message) {
        System.out.println("Broadcast to room " + roomNumber + ": " + message.toString());
        messagingTemplate.convertAndSend("/topic/room/" + roomNumber, message);
    }

}
