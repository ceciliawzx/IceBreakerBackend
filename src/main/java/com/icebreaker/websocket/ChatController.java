package com.icebreaker.websocket;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@Controller
public class ChatController {
    @MessageMapping("/room/{roomNumber}/sendMessage")
    @SendTo("/topic/room/{roomNumber}")
    public ChatMessage handleMessage(@DestinationVariable("roomNumber") int roomNumber, @Payload ChatMessage message) {
        System.out.println("handleMessage has been triggered, received message: " + message.toString());
        message.setContent("Server has received your message: " + message.getContent());
        return message;
    }
}
