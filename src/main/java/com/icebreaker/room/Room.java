package com.icebreaker.room;

import lombok.Getter;
import com.icebreaker.person.*;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final int MAX_CAPACITY = 10;
    @Getter
    private final int roomNumber;
    @Getter
    private final String roomCode;
    private final List<Person> players = new ArrayList<>(); // All players including the host. Host is at position 0
    @Getter
    private final Admin host;

    public Room(int roomNumber, String roomCode, Admin host) {
        this.roomNumber = roomNumber;
        this.roomCode = roomCode;
        this.host = host;
        players.add(host);
    }

    public boolean joinRoom(User user) {
        players.add(user);
        return true;
    }

    public void startRoom() {

    }

    public boolean addUser(User user) {
        players.add(user);
        return true;
    }

    public List<Person> getPlayers() {
        return players;
    }

    public Person getPlayer(String userID) {
        for (Person p : players) {
            if (p.getId() == userID) {
                return p;
            }
        }
        return null;
    }
}
