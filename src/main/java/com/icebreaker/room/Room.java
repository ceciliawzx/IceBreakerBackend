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
    private final List<User> players = new ArrayList<>(); // All players including the host. Host is at position 0
    // private final Admin host;

    public Room(int roomNumber, String adminID) {
        this.roomNumber = roomNumber;

    }

    public boolean joinRoom(HttpServletRequest request) {
        return true;
    }

    public void startRoom() {

    }

    public boolean addUser(User user) {
        players.add(user);
        return true;
    }
}
