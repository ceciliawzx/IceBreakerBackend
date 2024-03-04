package com.icebreaker.services;

import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.websocket.BackMessage;
import com.icebreaker.websocket.DrawingMessage;
import com.icebreaker.websocket.ModalMessage;
import com.icebreaker.websocket.PasteImgMessage;
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
        System.out.println("Broadcast drawing to room " + roomCode + ": " + message.toString());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", message);
    }

    public void broadcastPasteImg(String roomCode, PasteImgMessage message) {
        System.out.println("Broadcast pasteImg to room " + roomCode + ": " + message.toString());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", message);
    }

    public void returnToPresentingRoom(String roomCode) {
        BackMessage backMessage = new BackMessage(roomCode);
        // Reset guessedList
        runner.resetGuessedList(roomCode);
        System.out.println("Send return to presenting room back message to Pictionary Room");
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", backMessage);
    }

    public void showModal(String roomCode) {
        ModalMessage modalMessage = new ModalMessage(roomCode, true);
        System.out.println("Send show modal message to Pictionary Room");
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/drawing", modalMessage);
    }

    public void addCorrectGuesser(String roomCode, String guesserId) {
        runner.addCorrectGuesser(roomCode, guesserId);
        if (runner.allGuessed(roomCode)) {
            showModal(roomCode);
        }
    }

}
