package com.icebreaker.httprequests;

import com.icebreaker.room.Room;
import com.icebreaker.serverrunner.ServerRunner;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = "*")
@RestController
public class HttpRequestsHandler {

    private final AtomicInteger roomNumber = new AtomicInteger(0);

    @GetMapping("/myEndpoint")
    public String handleRequest(@RequestParam(name = "message", required = false) String message) {
        return "Received message: " + message;
    }

    @GetMapping("/createRoom")
    public String handleRoomCreation(@RequestParam(name = "message", required = false) String message,
                                     HttpServletRequest request) {
        int newRoomNumber = roomNumber.getAndIncrement();
        Room newRoom = new Room(newRoomNumber, request);
        ServerRunner runner = ServerRunner.getInstance();
        return runner.addRoom(newRoom) ? "Room Created!!! Your New Room Number is " + newRoomNumber :
                "Room Creation Failed";
    }

    @GetMapping("/joinRoom")
    public String handleJoinRoom(@RequestParam(name = "roomNumber", required = true) int number,
                                 HttpServletRequest request) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.joinRoom(number, request) ?
                "You have joined room " + number + ". Your IP address is " + request.getRemoteAddr() :
                "Join Room Failed";
    }

    @GetMapping("/destroyRoom")
    public String handleDestroyRoom(@RequestParam(name = "roomNumber", required = true) int number) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.destroyRoom(number) ?
                "You have deleted room " + number : "Room Deletion Failed. No Such Active Room.";
    }
}
