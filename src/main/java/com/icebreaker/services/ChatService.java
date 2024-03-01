package com.icebreaker.services;

import com.icebreaker.room.Room;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.websocket.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final DrawingService drawingService;
    private final ServerRunner runner;

    @Autowired
    public ChatService(SimpMessagingTemplate messagingTemplate, DrawingService drawingService) {
        this.messagingTemplate = messagingTemplate;
        this.drawingService = drawingService;
        this.runner = ServerRunner.getInstance();
    }

    public void broadcastToRoom(String roomCode, ChatMessage message) {
        System.out.println("Broadcast to room " + roomCode + ": " + message.toString());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, message);
    }

    public void checkGuessWord(String roomCode, ChatMessage message) {
        Room room = runner.getRoom(roomCode);
        Target target = room.getTarget();
        if (target.getTargetWord().equalsIgnoreCase(message.getContent())) {
            addCorrectGuesser(roomCode, message);
        }
    }

    public void addCorrectGuesser(String roomCode, ChatMessage message) {
        String guesserId = message.getSenderId();
        drawingService.addCorrectGuesser(roomCode, guesserId);
        message.setContent(message.getSender() + " has guessed right!");
        message.setSender("System");
    }

}
