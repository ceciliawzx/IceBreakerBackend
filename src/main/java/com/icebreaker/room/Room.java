package com.icebreaker.room;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import com.icebreaker.person.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final int MAX_CAPACITY = 10;
    @Getter
    private final int roomNumber;
    private final List<User> players = new ArrayList<User>(); // All players including the host. Host is at position 0
    // private final Admin host;
    private final List<HttpServletRequest> userAddresses = new ArrayList<HttpServletRequest>();

    public Room(int roomNumber, HttpServletRequest request) {
        this.roomNumber = roomNumber;
        userAddresses.add(request);
    }

    public boolean joinRoom(HttpServletRequest request) {
        userAddresses.add(request);
        return true;
    }

    public void startRoom() {

    }

    public int getRoomNumber() {
        return roomNumber;
    }
}
