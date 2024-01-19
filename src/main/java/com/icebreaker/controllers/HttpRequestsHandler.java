package com.icebreaker.controllers;

import com.icebreaker.person.User;
import com.icebreaker.room.Room;
import com.icebreaker.serverrunner.ServerRunner;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class HttpRequestsHandler {

    private final AtomicInteger roomNumber = new AtomicInteger(0);
    private final AtomicInteger userID = new AtomicInteger(0);

    @GetMapping("/myEndpoint")
    public String handleRequest(@RequestParam(name = "message", required = false) String message) {
        return "Received message: " + message;
    }

    @PostMapping("/createRoom")
    public String handleRoomCreation(@RequestParam(name = "name", required = true) String name)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        Integer newRoomNumber = roomNumber.getAndIncrement();

        Integer newUserID = userID.getAndIncrement();
        String nameID = name + newUserID;
        md.update(nameID.getBytes());
        byte[] userBytes = md.digest();
        StringBuilder usb = new StringBuilder();
        for (byte hashByte : userBytes) {
            usb.append(String.format("%02x", hashByte));
        }

        System.out.println("User ID: " + usb);

        ServerRunner runner = ServerRunner.getInstance();
        String roomCode = runner.getRoomCodeGenerator().generateUniqueCode();
        Room newRoom = new Room(newRoomNumber, roomCode, usb.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(Map.of("userID", usb.toString(), "roomCode", roomCode));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        return runner.addRoom(newRoom, roomCode) ? json : "Room Creation Failed";
    }

    @PostMapping("/joinRoom")
    public String handleJoinRoom(@RequestParam(name = "roomCode", required = true) String code,
                                 @RequestParam(name = "name", required = true) String name)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        Integer newUserID = userID.getAndIncrement();
        String nameID = name + newUserID;
        md.update(nameID.getBytes());
        byte[] userBytes = md.digest();
        StringBuilder usb = new StringBuilder();
        for (byte hashByte : userBytes) {
            usb.append(String.format("%02x", hashByte));
        }

        System.out.println("User ID: " + usb);

        ServerRunner runner = ServerRunner.getInstance();

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(Map.of("userID", usb.toString()));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        return runner.joinRoom(code, usb.toString()) ?
                json :
                "Join Room Failed";
    }

    @DeleteMapping("/destroyRoom")
    public String handleDestroyRoom(@RequestParam(name = "roomNumber", required = true) int number) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.destroyRoom(number) ?
                "You have deleted room " + number : "Room Deletion Failed. No Such Active Room.";
    }

    @PutMapping("/addPerson")
    public ResponseEntity<String> createPerson(@RequestBody User user) {
        ServerRunner runner = ServerRunner.getInstance();
        runner.roomAddUser(user);
        return new ResponseEntity<>("Person updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/kickPerson")
    public ResponseEntity<String> kickPerson(@RequestParam(name = "userID", required = true) int userID,
                                             @RequestParam(name = "roomID", required = true) int roomID) {
        // TODO
        return new ResponseEntity<>("Kicked user: " + userID + " From room: " + roomID, HttpStatus.OK);
    }
}