package com.icebreaker.services;

import com.icebreaker.dto.room.Room;
import com.icebreaker.enums.RoomStatus;
import com.icebreaker.dto.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.dto.websocket.BackMessage;
import com.icebreaker.dto.websocket.DrawingMessage;
import com.icebreaker.dto.websocket.ModalMessage;
import com.icebreaker.dto.websocket.PasteImgMessage;
import com.icebreaker.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DrawingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ServerRunner runner;
    private final WaitRoomService waitRoomService;

    @Autowired
    public DrawingService(SimpMessagingTemplate messagingTemplate, WaitRoomService waitRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.waitRoomService = waitRoomService;
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

    public boolean startDrawAndGuess(String roomCode, String fieldName, String targetWord) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.PICTURING)) {
            runner.setTargetInRoom(roomCode, new Target(fieldName, targetWord));
            waitRoomService.broadcastMessage(roomCode);
            return true;
        }
        return false;
    }

    public String getTarget(String roomCode) {
        Room room = runner.getRoom(roomCode);
        if (room == null) {
            return JsonUtils.returnRoomNotFoundJsonError();
        }
        Target target = room.getTarget();
        return JsonUtils.returnJson(Map.of("target", target), "Error fetching target of a room");
    }

    public boolean startShareBoard(String roomCode, String fieldName) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.SHAREBOARD)) {
            runner.setTargetInRoom(roomCode, new Target(fieldName, ""));
            waitRoomService.broadcastMessage(roomCode);
            return true;
        }
        return false;
    }
}
