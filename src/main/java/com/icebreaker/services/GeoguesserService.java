package com.icebreaker.services;

import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.GeoguesserStatus;
import com.icebreaker.websocket.BackMessage;
import com.icebreaker.websocket.DrawingMessage;
import com.icebreaker.websocket.GeoguesserMessage;
import com.icebreaker.websocket.HangmanMessage;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.glassfish.grizzly.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.toRadians;

@Service
public class GeoguesserService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ServerRunner runner;

    @Autowired
    public GeoguesserService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.runner = ServerRunner.getInstance();
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
