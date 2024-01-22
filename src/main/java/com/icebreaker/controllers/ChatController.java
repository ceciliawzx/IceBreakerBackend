package com.icebreaker.controllers;

import com.icebreaker.services.ChatService;
import com.icebreaker.websocket.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    @Autowired
    public ChatController(ChatService chatService) {
    }

    @MessageMapping("/room/{roomNumber}/sendMessage")
    @SendTo("/topic/room/{roomNumber}")
    public ChatMessage handleMessage(@Payload ChatMessage message) {
        // Handle the message, knowing which user it came from
        System.out.println("handleMessage has been triggered, received message: " + message.toString());
        message.setContent("Server has received your message: " + message.getContent());
        return message;
    }

}
