package com.icebreaker.controllers;
import com.icebreaker.websocket.ChatMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BroadcastScheduler {

    private final ChatController chatController;

    public BroadcastScheduler(ChatController chatController) {
        this.chatController = chatController;
    }

    @Scheduled(fixedRate = 5000) // 5000 milliseconds = 5 seconds
    public void broadcastTestMessage() {
        ChatMessage testMessage = new ChatMessage();
        testMessage.setContent("This is a scheduled test broadcast message from server!");
        testMessage.setTimestamp(LocalDateTime.now());
        testMessage.setRoomNumber(0);
        testMessage.setSender("Server");
        testMessage.setSender("ServerId");
        chatController.broadcastToRoom(0, testMessage); // Broadcasting to room 0
    }
}
