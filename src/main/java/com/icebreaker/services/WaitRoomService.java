package com.icebreaker.services;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WaitRoomService {

    private final ServerRunner runner = ServerRunner.getInstance();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WaitRoomService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastMessage(String roomCode) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/wait", true);
    }

    public boolean forceBackToAllPresentedRoom(String roomCode) {
        if (runner.forceBackToAllPresentedRoom(roomCode)) {
            broadcastMessage(roomCode);
            return true;
        }
        return false;
    }

    public boolean backToWaitRoom(String roomCode) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.WAITING)) {
            // Reset Target
            runner.setTargetInRoom(roomCode, new Target("", ""));
            // add presented person to presentedList
            runner.addToPresentedList(roomCode);
            // Reset presentRoomInfo when back to WaitRoom
            boolean result = runner.resetPresentRoomInfo(roomCode);
            // Broadcast re-fetch message
            broadcastMessage(roomCode);
            return result;
        }
        return false;
    }
}
