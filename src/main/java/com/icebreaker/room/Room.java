package com.icebreaker.room;

import com.icebreaker.services.ChatService;
import com.icebreaker.utils.Constants;
import com.icebreaker.websocket.ChatMessage;
import lombok.Getter;
import com.icebreaker.person.*;
import lombok.NonNull;
import lombok.Setter;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final int MAX_CAPACITY = 10;
    @Getter
    private final int roomNumber;
    @Getter
    private final String roomCode;
    @Getter
    private final List<Person> players = new ArrayList<>(); // All players including the host. Host is at position 0
    private final List<Person> presentedList = new ArrayList<>();
    @Getter
    private final Admin host;
    @Getter
    private Person presenter;
    @Getter
    @Setter
    private RoomStatus roomStatus;
    @Getter
    @Setter
    private String target;
    @Getter
    @Setter
    private PresentRoomInfo presentRoomInfo;

    public Room(int roomNumber, String roomCode, Admin host) {
        this.roomNumber = roomNumber;
        this.roomCode = roomCode;
        this.host = host;
        this.presenter = host;
        this.players.add(host);
        this.roomStatus = RoomStatus.WAITING;
        this.presentRoomInfo = new PresentRoomInfo();
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

            this.host.setDisplayName(person.getDisplayName());
            this.host.setRoomCode(person.getRoomCode());
            this.host.setUserID(person.getUserID());
            this.host.setProfileImage(person.getProfileImage());
            this.host.setFirstName(person.getFirstName());
            this.host.setLastName(person.getLastName());
            this.host.setCountry(person.getCountry());
            this.host.setCity(person.getCity());
            this.host.setFeeling(person.getFeeling());
            this.host.setFavFood(person.getFavFood());
            this.host.setFavActivity(person.getFavActivity());
            this.host.setCompleted(person.isCompleted());

            players.remove(0);
            players.add(0, person);
        } else {
            for (int i = 1; i < players.size(); i++) {
                if (person.getUserID().equals(players.get(i).getUserID())) {
                    players.remove(i);
                    players.add(person);
                }
            }
        }

        if (person.getUserID().equals(this.presenter.getUserID())) {
            this.presenter = person;
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
                return true;
            }
        }
        return false;
    }

    public boolean addToPresentedList(Person newPresenter) {
        presentedList.add(presenter);
        this.presenter = newPresenter;
        return true;
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

    public List<GameType> getAvailableGames(String userID, String fieldName) {
        Person person = null;
        for (Person player : players) {
            if (player.getUserID().equals(userID)) {
                person = player;
                break;
            }
        }

        try {
            Field field = Person.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(person);

            if (fieldValue instanceof String value) {
                return checkGames(fieldName, value);
            } else {
                return null;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<GameType> checkGames(String fieldName, String value) {
        List<GameType> games = new ArrayList<>();
        games.add(GameType.REVEAL);
        switch (fieldName) {
            case "country", "city" -> {
                games.add(GameType.GEOGUESSER);
                if (value.length() <= 7 && value.length() >= 4) {
                    games.add(GameType.WORDLE);
                }
                games.add(GameType.PICTIONARY);
                games.add(GameType.SHAREBOARD);
                games.add(GameType.HANGMAN);
            }
            case "favFood", "favActivity" -> {
                if (value.length() <= 7 && value.length() >= 4) {
                    games.add(GameType.WORDLE);
                }
                games.add(GameType.PICTIONARY);
                games.add(GameType.SHAREBOARD);
                games.add(GameType.HANGMAN);
            }
            default -> {
                return null;
            }
        }
        return games;
    }

    public String getFieldValue(String userID, String fieldName) {
        Person person = null;
        for (Person player: players) {
            if (player.getUserID().equals(userID)) {
                person = player;
                break;
            }
        }

        try {
            Field field = Person.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object fieldValue = field.get(person);

            if (fieldValue instanceof String) {
                return (String) fieldValue;
            } else {
                return null;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
