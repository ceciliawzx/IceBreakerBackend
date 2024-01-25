package com.icebreaker.controllers;

import com.icebreaker.person.Admin;
import com.icebreaker.person.Person;
import com.icebreaker.room.Room;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    private final ChatService chatService;

    @Autowired
    public HttpRequestsHandler(ChatService chatService) {
        this.chatService = chatService;
    }
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

        int newRoomNumber = roomNumber.getAndIncrement();

        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);
        ServerRunner runner = ServerRunner.getInstance();
        String roomCode = runner.getRoomCodeGenerator().generateUniqueCode();
        Room newRoom = new Room(newRoomNumber, roomCode, new Admin(name, roomCode, usb.toString()), chatService);

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

        int newUserID = userID.getAndIncrement();
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
    public boolean handleDestroyRoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.destroyRoom(roomCode);
    }

    @GetMapping("/isAdmin")
    public boolean checkUserInRoom(@RequestParam("userID") String userID,
                                   @RequestParam("roomCode") String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.isAdmin(userID, roomCode);
    }

    @PostMapping(path = "/updatePerson", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String updatePerson(@RequestBody Person person) {
        ServerRunner runner = ServerRunner.getInstance();
        if (runner.roomUpdateUser(person)) {
            return "Success";
        } else {
            return "Fail";
        }
    }

    @DeleteMapping("/kickPerson")
    public boolean kickPerson(@RequestParam(name = "userID", required = true) String userID,
                                             @RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.kickPerson(roomCode, userID);
    }

    @GetMapping("/getPlayers")
    public String getPlayersInARoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        List<Person> players = runner.getPlayersInRoom(roomCode);
        if (players != null && !players.isEmpty()) {
            Person admin = players.get(0);
            List<Person> users = players.subList(1, players.size());
            boolean gameStatus = runner.getStatus(roomCode) == 1;
            ObjectMapper objectMapper = new ObjectMapper();
            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("admin", admin, "otherPlayers", users, "gameStatus", gameStatus));
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

    @PostMapping("/startInput")
    public String startInput(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        if (runner.serverStartRoom(roomCode)) {
            return "Success";
        }
        return "Fail";
    }

    @GetMapping("/infoComplete")
    public boolean checkPlayerInfoComplete(@RequestParam(name = "roomCode", required = true) String roomCode,
                                           @RequestParam(name = "userID", required = true) String userID) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.checkPlayerInfoComplete(roomCode, userID);
    }
}
