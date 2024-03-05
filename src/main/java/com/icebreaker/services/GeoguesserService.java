package com.icebreaker.services;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.GeoguesserStatus;
import com.icebreaker.utils.JsonUtils;
import com.icebreaker.websocket.BackMessage;
import com.icebreaker.websocket.GeoguesserMessage;
import com.icebreaker.websocket.ModalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeoguesserService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    @Autowired
    public GeoguesserService(SimpMessagingTemplate messagingTemplate, WaitRoomService waitRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.waitRoomService = waitRoomService;
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

    public boolean startGeoguesser(String roomCode, String fieldName) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.GEO_GUESSING)) {
            runner.resetGeoguesser(roomCode);
            runner.setField(roomCode, fieldName);
            waitRoomService.broadcastMessage(roomCode);
            return true;
        }
        return false;
    }

    public String getGeoguesserStatus(String roomCode) {
        if (runner.containsRoom(roomCode)) {
            return JsonUtils.returnJson(Map.of("status", runner.getGeoguesserStatus(roomCode)), JsonUtils.returnJsonError("Serialization error"));
        }
        return JsonUtils.roomNotFound;
    }

    public boolean setTargetLocation(String roomCode, String location, String userID) {
        boolean isSet = runner.setTargetLocation(roomCode, location, userID);
        waitRoomService.broadcastMessage(roomCode);
        return isSet;
    }

    public boolean getUserGeoSubmission(String roomCode, String userID) {
        return !runner.checkNotSubmission(roomCode, userID);
    }

    public String geoGuesserRank(String roomCode) {
        if (runner.containsRoom(roomCode)) {
            return JsonUtils.returnJson(Map.of("winner", runner.geoGuesserWinner(roomCode), "rankPerson", runner.geoGuesserPersonRank(roomCode), "rankDistance", runner.geoGuesserDistanceRank(roomCode)), JsonUtils.returnJsonError("Serialisation error"));
        }
        return JsonUtils.returnRoomNotFoundJsonError();
    }

    public String presenterLocation(String roomCode) {
        return runner.presenterLocation(roomCode);
    }

    public String geoguesserFieldName(String roomCode) {
        return runner.geoGuesserFieldName(roomCode);
    }

    public boolean geoguesserForceEnd(String roomCode) {
        return runner.setGeoStatusInRoom(roomCode, GeoguesserStatus.SUBMITTED);
    }
}
