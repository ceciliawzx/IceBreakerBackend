package com.icebreaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WaitRoomService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WaitRoomService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    public void broadcastPeopleInfoChange(String roomCode) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wait", true);
    }
}
