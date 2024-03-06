package com.icebreaker.services;

import com.icebreaker.dto.room.Room;
import com.icebreaker.enums.RoomStatus;
import com.icebreaker.dto.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.dto.websocket.ChatMessage;
import com.icebreaker.dto.websocket.TimerMessage;
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
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/chatRoom", message);
    }

    public void handleMessage(ChatMessage message) {
        // Draw & Guess: handle guess messages
        // When the RoomStatus changes to PICTURING, any message sent to server will be regarded as a guess
        String roomCode = message.getRoomCode();
        if (runner.getStatus(roomCode) == RoomStatus.PICTURING) {
            checkGuessWord(roomCode, message);
        } else {
            // Otherwise, just broadcast the chat message
            message.setContent("Server has received your message: " + message.getContent());
        }
        broadcastToRoom(roomCode, message);
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

    public void broadCastTimerStarted(String roomCode) {
        TimerMessage timerMessage = new TimerMessage();
        timerMessage.setStarted(true);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/chatRoom", timerMessage);
    }

}
