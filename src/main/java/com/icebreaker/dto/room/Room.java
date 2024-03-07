package com.icebreaker.dto.room;

import com.icebreaker.dto.person.Admin;
import com.icebreaker.dto.person.Person;
import com.icebreaker.dto.person.User;
import com.icebreaker.enums.GameType;
import com.icebreaker.enums.GeoguesserStatus;
import com.icebreaker.enums.RoomStatus;
import com.icebreaker.utils.Geoguesser;
import lombok.Getter;
import lombok.Setter;
import org.glassfish.grizzly.utils.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Room {

    @Getter
    private final int roomNumber;
    @Getter
    private final String roomCode;
    private final List<Person> players = new ArrayList<>(); // All players including the host. Host is at position 0
    private final List<Person> presentedList = new ArrayList<>();
    @Getter
    private List<String> correctlyGuessedPlayerIds = new ArrayList<>();
    @Getter
    private final Admin host;
    @Getter
    private Person presenter;
    @Getter
    @Setter
    private RoomStatus roomStatus;
    @Getter
    @Setter
    private Target target = new Target("", "");
    @Getter
    @Setter
    private PresentRoomInfo presentRoomInfo;
    private Geoguesser geoguesser;
    private final List<String> notifyIDs = new ArrayList<>();
    @Getter
    @Setter
    private boolean showTimerModal = true;

    public Room(int roomNumber, String roomCode, Admin host) {
        this.roomNumber = roomNumber;
        this.roomCode = roomCode;
        this.host = host;
        this.presenter = host;
        this.players.add(host);
        this.roomStatus = RoomStatus.WAITING;
        this.presentRoomInfo = new PresentRoomInfo();
        this.geoguesser = new Geoguesser(GeoguesserStatus.PRE_CHOOSE);
    }

    public void startRoom() {
        this.roomStatus = RoomStatus.PRESENTING;
    }

    public boolean joinRoom(User user) {
        if (roomStatus != RoomStatus.WAITING) {
            return false;
        }
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
                    break;
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
        for (Person p : players) {
            if (userID.equals(p.getUserID())) {
                return p;
            }
        }
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
                // Reset presentRoomInfo when change a presenter
                resetPresentRoomInfo();
                return true;
            }
        }
        return false;
    }

    public boolean addToPresentedList() {
        if (checkPlayerInfoComplete(this.presenter.getUserID())) {
            presentedList.add(presenter);
        }
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
                if (checkForWorldle(value)) {
                    games.add(GameType.WORDLE);
                }
                games.add(GameType.PICTIONARY);
                if (checkForHangman(value)) {
                    games.add(GameType.HANGMAN);
                }
                games.add(GameType.SHAREBOARD);
            }
            case "favFood", "favActivity", "feeling" -> {
                if (checkForWorldle(value)) {
                    games.add(GameType.WORDLE);
                }
                games.add(GameType.PICTIONARY);
                games.add(GameType.SHAREBOARD);
                if (checkForHangman(value)) {
                    games.add(GameType.HANGMAN);
                }
            }
            default -> {
                return games;
            }
        }
        return games;
    }

    private boolean checkForHangman(String value) {
        return value.matches("[a-zA-Z ]+");
    }

    private boolean checkForWorldle(String value) {
        if (value.length() <= 7 && value.length() >= 4) {
            if (!value.contains(" ")) {
                return value.matches("[a-zA-Z]+");
            }
        }
        return false;
    }

    public String getFieldValue(String userID, String fieldName) {
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

    public boolean setLocation(String location, String userID) {
        if (userID.equals(this.presenter.getUserID())) {
            return this.geoguesser.startGame(location);
        } else {
            boolean result = this.geoguesser.makeGuess(userID, location);
            if (geoguesser.answersSumitted() >= players.size() - 1) {
                setGeoStatus(GeoguesserStatus.SUBMITTED);
            }
            return result;
        }
    }

    public void setGeoStatus(GeoguesserStatus status) {
        this.geoguesser.setStatus(status);
    }

    public GeoguesserStatus getGeoStatus() {
        return this.geoguesser.getStatus();
    }

    public boolean checkNotSubmitted(String userID) {
        if (userID.equals(this.presenter.getUserID())) {
            return this.geoguesser.getStatus().equals(GeoguesserStatus.PRE_CHOOSE);
        } else {
            return this.geoguesser.checkNotSubmitted(userID);
        }
    }

    public List<Person> geoGuesserWinner() {
        List<String> winnerIDs = this.geoguesser.geoGuesserWinner();
        List<Person> winners = new ArrayList<>();

        for (String winnerID : winnerIDs) {
            Person person = findPersonByID(winnerID);
            if (person != null) {
                winners.add(person);
            }
        }

        return winners;
    }

    private Person findPersonByID(String id) {
        for (Person person : players) {
            if (person.getUserID().equals(id)) {
                return person;
            }
        }
        return null;
    }

    public List<Person> geoGuesserPersonRank() {
        List<Pair<String, Double>> rank = this.geoguesser.geoGuesserRank();
        List<String> playerIDs = new ArrayList<>();
        List<Person> playerRankList = new ArrayList<>();

        for (Pair<String, Double> stringDoublePair : rank) {
            playerIDs.add(stringDoublePair.getFirst());
        }

        for (String playerID : playerIDs) {
            Person person = findPersonByID(playerID);
            if (person != null) {
                playerRankList.add(person);
            }
        }
        return playerRankList;
    }

    public List<Double> geoGuesserDistanceRank() {
        List<Pair<String, Double>> rank = this.geoguesser.geoGuesserRank();
        List<Double> distances = new ArrayList<>();

        for (Pair<String, Double> stringDoublePair : rank) {
            distances.add(stringDoublePair.getSecond());
        }

        return distances;
    }

    public String presenterLocation() {
        return this.geoguesser.getLocation();
    }

    public List<Person> getOtherPlayers() {
        List<Person> res = new ArrayList<>();
        for (Person player : players) {
            if (!player.getUserID().equals(host.getUserID()) && !player.getUserID().equals(presenter.getUserID())) {
                res.add(player);
            }
        }
        return res;
    }

    public List<String> getOtherPlayersIds() {
        List<String> res = new ArrayList<>();
        for (Person player : getOtherPlayers()) {
            res.add(player.getUserID());
        }
        return res;
    }

    public boolean allGuessed() {
        System.out.println("all guessed: other players, " + getOtherPlayersIds().toString() + "correctly guessed: " + correctlyGuessedPlayerIds.toString());
        for (String id : getOtherPlayersIds()) {
            if (!correctlyGuessedPlayerIds.contains(id)) {
                return false;
            }
        }
        return true;
    }

    public boolean notifyPeople(String userID) {
        if (!notifyIDs.contains(userID)) {
            notifyIDs.add(userID);
            return true;
        }
        return false;
    }

    public boolean isNotified(String userID) {
        boolean temp = notifyIDs.contains(userID);
        if (temp) {
            int count = 0;
            while (!acknowledgeNotification(userID) && count < 100) {
                count++;
            }
        }
        return temp;
    }

    private boolean acknowledgeNotification(String userID) {
        if (notifyIDs.contains(userID)) {
            notifyIDs.remove(userID);
            return true;
        }
        return false;
    }

    public void resetGuessedList() {
        correctlyGuessedPlayerIds = new ArrayList<>();
    }

    public void resetPresentRoomInfo() {
        System.out.println("Resetting presentRoomInfo in room " + roomCode);
        this.presentRoomInfo = new PresentRoomInfo();
    }

    public void resetGeoguesser() {
        this.geoguesser = new Geoguesser(GeoguesserStatus.PRE_CHOOSE);
    }

    public void setField(String fieldName) {
        this.geoguesser.setFieldName(fieldName);
    }

    public String getGeoGuesserFieldName() {
        return this.geoguesser.getFieldName();
    }

}
