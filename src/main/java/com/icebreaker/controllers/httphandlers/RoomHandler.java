package com.icebreaker.controllers.httphandlers;

import com.icebreaker.person.Admin;
import com.icebreaker.room.Room;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
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
    public String handleRoomCreation(@RequestParam(name = "name") String name)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        int newRoomNumber = roomNumber.getAndIncrement();

        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);
        String roomCode = runner.getRoomCodeGenerator().generateUniqueCode();
        Admin admin = new Admin(name, roomCode, usb.toString());
        Room newRoom = new Room(newRoomNumber, roomCode, admin);
        System.out.printf("Create Room, Display Name: %s, UserID: %s, RoomCode: %s%n", name, usb, roomCode);
        return runner.addRoom(newRoom, roomCode) ? JsonUtils.returnJson(
                Map.of("userID", usb.toString(), "roomCode", roomCode), "Room Creation Failed"
        ) : "Room Creation Failed";
    }

    @PostMapping("/joinRoom")
    public String handleJoinRoom(@RequestParam(name = "roomCode") String code,
                                 @RequestParam(name = "name") String name)
            throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);

        return runner.joinRoom(code, name, usb.toString()) ? JsonUtils.returnJson(
                Map.of("userID", usb.toString()), "Join Room Failed"
        ) : "Join Room Failed";

    }

    @DeleteMapping("/destroyRoom")
    public boolean handleDestroyRoom(@RequestParam(name = "roomCode") String roomCode) {
        System.out.printf("Destroy Room: %s%n", roomCode);
        return runner.destroyRoom(roomCode);
    }
}
