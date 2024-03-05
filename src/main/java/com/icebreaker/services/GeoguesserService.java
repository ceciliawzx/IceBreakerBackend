package com.icebreaker.services;

import com.icebreaker.websocket.BackMessage;
import com.icebreaker.websocket.GeoguesserMessage;
import com.icebreaker.websocket.ModalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GeoguesserService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GeoguesserService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastGuessing(String roomCode, GeoguesserMessage message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/geoguesser", message);
    }

    public void returnToPresentingRoom(String roomCode) {
        BackMessage backMessage = new BackMessage(roomCode);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/geoguesser", backMessage);
    }

    public void showModal(String roomCode) {
        ModalMessage modalMessage = new ModalMessage(roomCode, true);
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/geoguesser", modalMessage);
    }
}
