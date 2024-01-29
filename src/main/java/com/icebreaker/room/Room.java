package com.icebreaker.room;

import com.icebreaker.services.ChatService;
import com.icebreaker.websocket.ChatMessage;
import lombok.Getter;
import com.icebreaker.person.*;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final int MAX_CAPACITY = 10;
    @Getter
    private final int roomNumber;
    @Getter
    private final String roomCode;
    private final List<Person> players = new ArrayList<>(); // All players including the host. Host is at position 0
    private final List<Person> presentedList = new ArrayList<>();
    @Getter
    private final Admin host;
    @Getter
    private Person presenter;
    @Getter
    @Setter
    private RoomStatus roomStatus;
    private final ChatService chatService;

    public Room(int roomNumber, String roomCode, Admin host, ChatService chatService) {
        this.roomNumber = roomNumber;
        this.roomCode = roomCode;
        this.host = host;
        this.presenter = host;
        this.chatService = chatService;
        players.add(host);
        this.roomStatus = RoomStatus.WAITING;
    }

    public void startRoom() {
        this.roomStatus = RoomStatus.PRESENTING;
    }

    public boolean joinRoom(User user) {
        players.add(user);
        return true;
    }

    public boolean updateUser(Person person) {
        if (person.getUserID().equals(host.getUserID())) {
            players.remove(0);
            players.add(0, person);
        } else {
            for (int i = 1; i < players.size(); i++) {
                if (person.getUserID().equals(players.get(i).getUserID())) {
                    players.remove(i);
                    players.add(person);
                    break;
                }
            }
        }
        return true;
    }

    public List<Person> getPlayers() {
        return players;
    }

    public Person getPlayer(String userID) {
        System.out.println(userID);
        for (Person p : players) {
            if (userID.equals(p.getUserID())) {
                return p;
            }
        }
        System.out.println("Fail");
        return null;
    }

    public boolean kickPerson(String userID) {
        if (userID.equals(host.getUserID())) {
            return false;
        } else {
            for (Person p : players) {
                if (userID.equals(p.getUserID())) {
                    players.remove(p);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkPlayerInfoComplete(String userID) {
        for (Person p : players) {
            if (userID.equals(p.getUserID())) {
                return p.getProfileImage() != null &&
                        p.getFirstName() != null &&
                        p.getLastName() != null &&
                        p.getCountry() != null &&
                        p.getCity() != null &&
                        p.getFeeling() != null &&
                        p.getFavFood() != null &&
                        p.getFavActivity() != null;
            }
        }
        return false;
    }

    public boolean setPresenter(String userID) {
        for (Person person : players) {
            if (person.getUserID().equals(userID)) {
                presenter = person;
                presentedList.add(person);
                return true;
            }
        }
        return false;
    }

    public List<Person> getNotPresentedPeople() {
        List<Person> difference = new ArrayList<>();
        for (Person person : players) {
            if (!presentedList.contains(person)) {
                difference.add(person);
            }
        }

        return difference;
    }
}
