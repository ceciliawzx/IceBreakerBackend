package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.person.Admin;
import com.icebreaker.room.Room;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.icebreaker.utils.HashUserId.hashUserId;

@RestController
public class RoomHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final AtomicInteger roomNumber = new AtomicInteger(0);
    private final AtomicInteger userID = new AtomicInteger(0);

    @PostMapping("/createRoom")
    public String handleRoomCreation(@RequestParam(name = "name", required = true) String name)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        int newRoomNumber = roomNumber.getAndIncrement();

        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);
        String roomCode = runner.getRoomCodeGenerator().generateUniqueCode();
        Admin admin = new Admin(name, roomCode, usb.toString());
        Room newRoom = new Room(newRoomNumber, roomCode, admin);

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(Map.of("userID", usb.toString(), "roomCode", roomCode));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        System.out.printf("Create Room, Display Name: %s, UserID: %s, RoomCode: %s%n", name, usb.toString(), roomCode);

        return runner.addRoom(newRoom, roomCode) ? json : "Room Creation Failed";
    }

    @PostMapping("/joinRoom")
    public String handleJoinRoom(@RequestParam(name = "roomCode", required = true) String code,
                                 @RequestParam(name = "name", required = true) String name)
            throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(Map.of("userID", usb.toString()));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        System.out.printf("Join Room, Display Name: %s, UserID: %s, RoomCode: %s%n", name, usb.toString(), code);

        return runner.joinRoom(code, name, usb.toString()) ?
                json :
                "Join Room Failed";
    }

    @DeleteMapping("/destroyRoom")
    public boolean handleDestroyRoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        System.out.printf("Destroy Room: %s%n", roomCode);
        return runner.destroyRoom(roomCode);
    }
}
