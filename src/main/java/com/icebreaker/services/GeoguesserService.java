package com.icebreaker.services;

import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.websocket.BackMessage;
import com.icebreaker.websocket.GeoguesserMessage;
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
        System.out.println("Broadcast location to room " + roomCode + ": " + message.toString());
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/geoguesser", message);
    }

    public void returnToPresentingRoom(String roomCode) {
        BackMessage backMessage = new BackMessage(roomCode);
        System.out.println("Send return to presenting room back message to geoguessing Room");
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/geoguesser", backMessage);
    }
}
