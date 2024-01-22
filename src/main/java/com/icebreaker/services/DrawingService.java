package com.icebreaker.services;

import com.icebreaker.websocket.DrawingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DrawingService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public DrawingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastDrawing(String roomCode, DrawingMessage message) {
        System.out.println("Broadcast drawing to room " + roomCode + ": " + message.toString());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", message);
    }
}
