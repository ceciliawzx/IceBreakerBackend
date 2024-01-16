package com.icebreaker.room;

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
    private final Admin host;
    private final List<InetSocketAddress> userAddresses = new ArrayList<InetSocketAddress>();

    public Room(int roomNumber, Admin host) {
        this.roomNumber = roomNumber;
        this.host = host;
    }

    public boolean joinRoom() {
        // TODO
        return false;
    }
}
