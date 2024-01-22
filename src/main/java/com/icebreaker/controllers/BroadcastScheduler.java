package com.icebreaker.controllers;
import com.icebreaker.services.ChatService;
import com.icebreaker.websocket.ChatMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BroadcastScheduler {

    private final ChatService chatService;

    public BroadcastScheduler(ChatService chatService) {
        this.chatService = chatService;
    }

    // This is a test: the server will broadcast the test message every 5s
    @Scheduled(fixedRate = 5000) // 5000 milliseconds = 5 seconds
    public void broadcastTestMessage() {
        ChatMessage testMessage = new ChatMessage();
        testMessage.setContent("This is a scheduled test broadcast message from server!");
        testMessage.setTimestamp(LocalDateTime.now());
        testMessage.setRoomCode(0);
        testMessage.setSender("Server");
        testMessage.setSender("ServerId");
        chatService.broadcastToRoom("0", testMessage); // Broadcasting to room 0
    }
}
