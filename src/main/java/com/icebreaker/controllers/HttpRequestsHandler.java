package com.icebreaker.controllers;

import com.icebreaker.person.Admin;
import com.icebreaker.person.Person;
import com.icebreaker.person.User;
import com.icebreaker.room.Room;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.RoomCodeGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.icebreaker.utils.HashUserId.hashUserId;

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

        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);
        ServerRunner runner = ServerRunner.getInstance();
        String roomCode = runner.getRoomCodeGenerator().generateUniqueCode();
        Room newRoom = new Room(newRoomNumber, roomCode, new Admin(name, newRoomNumber, usb.toString()));

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
        StringBuilder usb = hashUserId(name, md, newUserID);

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

        return runner.joinRoom(code, name, usb.toString()) ?
                json :
                "Join Room Failed";
    }

    @DeleteMapping("/destroyRoom")
    public String handleDestroyRoom(@RequestParam(name = "roomNumber", required = true) int number) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.destroyRoom(number) ?
                "You have deleted room " + number : "Room Deletion Failed. No Such Active Room.";
    }

    @GetMapping("/isAdmin")
    public boolean checkUserInRoom(@RequestParam("userID") String userID,
                                   @RequestParam("roomCode") String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.isAdmin(userID, roomCode);
    }

    @PostMapping("/updatePerson")
    public ResponseEntity<String> updatePerson(@RequestBody Person person) {
        ServerRunner runner = ServerRunner.getInstance();
        runner.roomUpdateUser(person);
        return new ResponseEntity<>("Person updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/kickPerson")
    public ResponseEntity<String> kickPerson(@RequestParam(name = "userID", required = true) int userID,
                                             @RequestParam(name = "roomID", required = true) int roomID) {
        // TODO
        return new ResponseEntity<>("Kicked user: " + userID + " From room: " + roomID, HttpStatus.OK);
    }

    @GetMapping("/getPlayers")
    public String getPlayersInARoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        List<Person> players = runner.getPlayersInRoom(roomCode);
        if (players != null && !players.isEmpty()) {
            Person admin = players.get(0);
            List<Person> users = players.subList(1, players.size());
            ObjectMapper objectMapper = new ObjectMapper();
            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("admin", admin, "otherPlayers", users));
            } catch (Exception e) {
                // Handle exception if JSON serialization fails
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
            }

            return json;
        }
        return "Room can not be found";
    }
    // Im: userID    Out: User class

    @GetMapping("/getPlayer")
    public String getPlayerInARoom(@RequestParam(name = "roomCode", required = true) String roomCode,
                                   @RequestParam(name = "userID", required = true) String userID) {
        ServerRunner runner = ServerRunner.getInstance();
        Person person = runner.getOnePlayerInfo(roomCode, userID);
        if (person != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("userInfo", person));
            } catch (Exception e) {
                // Handle exception if JSON serialization fails
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
            }

            return json;
        }
        return "Person Not Found";
    }
}
