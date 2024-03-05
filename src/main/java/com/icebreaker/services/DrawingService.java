package com.icebreaker.services;

import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.websocket.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DrawingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ServerRunner runner;

    @Autowired
    public DrawingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.runner = ServerRunner.getInstance();
    }

    public void broadcastDrawing(String roomCode, DrawingMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", message);
    }

    public void broadcastPasteImg(String roomCode, PasteImgMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", message);
    }

    public void returnToPresentingRoom(String roomCode) {
        BackMessage backMessage = new BackMessage(roomCode);
        // Reset guessedList
        runner.resetGuessedList(roomCode);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", backMessage);
    }

    public void showModal(String roomCode) {
        ModalMessage modalMessage = new ModalMessage(roomCode, true);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", modalMessage);
    }

    public void addCorrectGuesser(String roomCode, String guesserId) {
        runner.addCorrectGuesser(roomCode, guesserId);
        if (runner.allGuessed(roomCode)) {
            showModal(roomCode);
        }
    }

}
