package com.icebreaker.controllers;

import com.icebreaker.enums.GameType;
import com.icebreaker.services.RoomService;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;


@RestController
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/createRoom")
    public String handleCreateRoom(@RequestParam(name = "name") String name)
            throws NoSuchAlgorithmException {
        return roomService.handleCreateRoom(name);
    }

    @PostMapping("/joinRoom")
    public String handleJoinRoom(@RequestParam(name = "roomCode") String code,
                                 @RequestParam(name = "name") String name)
            throws NoSuchAlgorithmException {
        return roomService.handleJoinRoom(code, name);
    }

    @DeleteMapping("/destroyRoom")
    public boolean handleDestroyRoom(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.handleDestroyRoom(roomCode);
    }

    @GetMapping("/availableGames")
    public List<GameType> availableGames(@RequestParam(name = "roomCode") String roomCode,
                                         @RequestParam(name = "userID") String userID,
                                         @RequestParam(name = "fieldName") String fieldName) {
        return roomService.availableGames(roomCode, userID, fieldName);
    }

    @GetMapping("/getPlayers")
    public String getPlayersInARoom(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.getPlayersInARoom(roomCode);
    }

    @GetMapping("/getPlayer")
    public String getPlayerInARoom(@RequestParam(name = "roomCode") String roomCode, @RequestParam(name = "userID") String userID) {
        return roomService.getPlayer(roomCode, userID);
    }

    @GetMapping("/getPresenter")
    public String getPresenterInARoom(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.getPresenterInARoom(roomCode);
    }

    @PostMapping("/pushNotification")
    public boolean pushNotification(@RequestParam(name = "roomCode") String roomCode, @RequestParam(name = "userID") String userID) {
        return roomService.pushNotification(roomCode, userID);
    }

    @PostMapping("/startPresenting")
    public boolean startPresenting(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.startPresenting(roomCode);
    }

    @GetMapping("/infoComplete")
    public boolean checkPlayerInfoComplete(@RequestParam(name = "roomCode") String roomCode,
                                           @RequestParam(name = "userID") String userID) {
        return roomService.checkPlayerInfoComplete(roomCode, userID);
    }

    @PostMapping("/changePresenter")
    public boolean changePresenter(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "userID") String userID) {
        return roomService.changePresenter(roomCode, userID);
    }

    @GetMapping("/notPresentedPeople")
    public String getNotPresentedPeople(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.getNotPresentedPeople(roomCode);
    }

    @GetMapping("/getPresentRoomInfo")
    public String getPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.getPresentRoomInfo(roomCode);
    }

    @PostMapping("setPresentRoomInfo")
    public boolean setPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode,
                                      @RequestParam(name = "field") String field) {
        return roomService.setPresentRoomInfo(roomCode, field);
    }

    @PostMapping("revealAllPresentRoomInfo")
    public boolean revealAllPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.revealAllPresentRoomInfo(roomCode);
    }

    @PostMapping("/backToPresentRoom")
    public boolean backToPresentRoom(@RequestParam(name = "roomCode") String roomCode) {
        return roomService.backToPresentRoom(roomCode);
    }

}
