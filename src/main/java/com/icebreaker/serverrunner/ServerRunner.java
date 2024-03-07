package com.icebreaker.serverrunner;

import com.icebreaker.dto.person.Admin;
import com.icebreaker.dto.person.Person;
import com.icebreaker.dto.person.User;
import com.icebreaker.dto.room.PresentRoomInfo;
import com.icebreaker.dto.room.Room;
import com.icebreaker.dto.room.Target;
import com.icebreaker.enums.GameType;
import com.icebreaker.enums.GeoguesserStatus;
import com.icebreaker.enums.RoomStatus;
import com.icebreaker.utils.Constants;
import com.icebreaker.utils.RoomCodeGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerRunner {

    private static ServerRunner instance;
    @Getter
    private final RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();

    private final Map<Room, Integer> activeRooms = new HashMap<>();
    private final Map<Integer, Room> roomNumbers = new HashMap<>();
    private final Map<String, Integer> codeNumberMapping = new HashMap<>();
    private final Map<Integer, String> numberCodeMapping = new HashMap<>();
    private final String mockRoomCode = "TEST";
    private Room mockRoom;
    private final Person mockBob = new User("Bobby", mockRoomCode, "1", Constants.getYellowDuck(),
                "Bob", "Li", "China", "Beijing", "Sad",
                 "Chocolate", "Badminton", true);
//            new User("Alexy", mockRoomCode, "2", Constants.getYellowDuck(),
//            "Alex", "Yang", "China", "Shanghai", "Sleepy",
//            "Sweet and sour chicken", "badminton", true);
    private final Person mockMohammed = new User("LuckyUser", mockRoomCode, "3", Constants.getYellowDuck(),
            "Lucky", "You", "United Kingdom", "Reading", "Excited",
            "Baked Potato", "Swimming", true);
    private final Person mockYHB = new User("Andersuki", mockRoomCode, "4", Constants.getYellowDuck(),
            "Hongbo", "Yu", "China", "Harbin", "Tired",
            "Steak", "Basketball", true);
    private final Person mockWSY = new User("SelinaWan666", mockRoomCode, "5", Constants.getYellowDuck(),
            "Siyu", "Wan", "Maldives", "Olhuveli", "Happy",
            "Nang", "Sing", true);
    private final Person mockZX = new User("Cecilia", mockRoomCode, "6", Constants.getYellowDuck(),
            "Zixi", "Wang", "china", "Shenzhen", "Joyful",
            "Cake", "Violin", true);
    private final Admin mockAdminAlex = new Admin("Alexy", mockRoomCode, "2", Constants.getYellowDuck(),
            "Alex", "Yang", "United Kingdom", "London", "Happy",
            "Fish and Chips", "Reading", true);
//            new Admin("Bobby", mockRoomCode, "1", Constants.getYellowDuck(),
//            "Bob", "Li", "China", "Beijing", "Sad",
//            "Chocolate", "Travel", true);
    private final Map<String, Person> mockRoomUserIDMap = new HashMap<>();

    private ServerRunner() {
        this.mockRoom = createMockRoom();
        mockRoomUserIDMap.put("2", mockBob);
        mockRoomUserIDMap.put("3", mockMohammed);
        mockRoomUserIDMap.put("4", mockYHB);
        mockRoomUserIDMap.put("5", mockWSY);
        mockRoomUserIDMap.put("6", mockZX);
    }

    private Room createMockRoom() {
        int mockRoomNumber = -1;
        Room mockRoom = new Room(mockRoomNumber, mockRoomCode, mockAdminAlex);
        addRoom(mockRoom, mockRoomCode);
        joinRoom(mockRoomCode, "Bobby", "2");
        joinRoom(mockRoomCode, "LuckyUser", "3");
        joinRoom(mockRoomCode, "Andersuki", "4");
        joinRoom(mockRoomCode, "SelinaWan666", "5");
        joinRoom(mockRoomCode, "Cecilia", "6");
        roomUpdateUser(mockBob);
        roomUpdateUser(mockMohammed);
        roomUpdateUser(mockYHB);
        roomUpdateUser(mockWSY);
        roomUpdateUser(mockZX);
        return mockRoom;
    }

    public boolean rejoinMockRoom(String userID) {
        if (mockRoom.getPlayer(userID) == null) {
            if (mockRoomUserIDMap.containsKey(userID)) {
                Person person = mockRoomUserIDMap.get(userID);
                joinRoom(mockRoomCode, person.getDisplayName(), userID);
                roomUpdateUser(person);
                return true;
            }
        }
        return false;
    }

    public static ServerRunner getInstance() {

        if (instance == null) {
            instance = new ServerRunner();
        }
        return instance;
    }


    // Return the room with roomNumber
    public Room getRoom(Integer roomNumber) {
        if (containsRoom(roomNumber)) {
            return roomNumbers.get(roomNumber);
        } else {
            System.out.printf("Cannot find the room with roomNumber %d", roomNumber);
            return null;
        }
    }

    // Return the room with roomCode
    public Room getRoom(String roomCode) {
        if (containsRoom(roomCode)) {
            return (roomNumbers.get(codeNumberMapping.get(roomCode)));
        } else {
            System.out.printf("Cannot find the room with roomCode %s", roomCode);
            return null;
        }
    }

    public boolean containsRoom(Room room) {
        synchronized (this) {
            return activeRooms.containsKey(room);
        }
    }

    public boolean containsRoom(String roomCode) {
        synchronized (this) {
            return codeNumberMapping.containsKey(roomCode);
        }
    }

    public boolean containsRoom(int roomNumber) {
        synchronized (this) {
            return roomNumbers.containsKey(roomNumber);
        }
    }

    public boolean addRoom(Room room, String code) {
        synchronized (this) {
            activeRooms.put(room, room.getRoomNumber());
            roomNumbers.put(room.getRoomNumber(), room);
            codeNumberMapping.put(code, room.getRoomNumber());
            numberCodeMapping.put(room.getRoomNumber(), code);
            return true;
        }
    }

    public boolean destroyRoom(String roomCode) {
        synchronized (this) {
            if (this.containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                activeRooms.remove(roomNumbers.get(roomNumber));
                roomNumbers.remove(roomNumber);
                codeNumberMapping.remove(roomCode);
                numberCodeMapping.remove(roomNumber);
                roomCodeGenerator.deleteUnUseCode(roomCode);
                return true;
            }
            return false;
        }
    }

    public boolean destroyRoom(int roomNumber) {
        synchronized (this) {
            if (this.containsRoom(roomNumber)) {
                activeRooms.remove(roomNumbers.get(roomNumber));
                roomNumbers.remove(roomNumber);
                codeNumberMapping.remove(numberCodeMapping.get(roomNumber), roomNumber);
                numberCodeMapping.remove(roomNumber);
                return true;
            }
            return false;
        }
    }

    public boolean joinRoom(String roomCode, String nickname, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).joinRoom(new User(nickname, roomCode, userID));
            }
            return false;
        }
    }


    public boolean roomUpdateUser(Person person) {
        synchronized (this) {
            if (containsRoom(person.getRoomCode())) {
                return getRoom(person.getRoomCode()).updateUser(person);
            }
            return false;
        }
    }

    public List<Person> getPlayersInRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getPlayers();
            }
            return null;
        }
    }

    public Admin getAdminInRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getHost();
            }
            return null;
        }
    }

    public List<Person> getOtherPlayersInRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                List<Person> otherPlayers = new ArrayList<>();
                int roomNumber = codeNumberMapping.get(roomCode);
                List<Person> players = roomNumbers.get(roomNumber).getPlayers();
                List<Person> playersWithoutAdmin = players.subList(1, players.size());
                Person presenter = roomNumbers.get(roomNumber).getPresenter();
                for (Person p : playersWithoutAdmin) {
                    if (!p.getUserID().equals(presenter.getUserID())) {
                        otherPlayers.add(p);
                    }
                }
                return otherPlayers;
            }
            return null;
        }
    }

    public Person getPresenterInRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getPresenter();
            }
            return null;
        }
    }

    public Person getOnePlayerInfo(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getPlayer(userID);
            }
            return null;
        }
    }

    public boolean isAdmin(String userID, String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return userID.equals(room.getHost().getUserID());
            }
            return false;
        }
    }

    public boolean isPresenter(String userID, String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return userID.equals(room.getPresenter().getUserID());
            }
            return false;
        }
    }

    public RoomStatus getStatus(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getRoomStatus();
            }
            return RoomStatus.NON_EXIST;
        }
    }


    public boolean kickPerson(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).kickPerson(userID);
            }
            return false;
        }
    }

    public boolean serverStartRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                getRoom(roomCode).startRoom();
                return true;
            }
            return false;
        }
    }

    public boolean checkPlayerInfoComplete(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).checkPlayerInfoComplete(userID);
            }
            return false;
        }
    }

    public boolean changeRoomStatus(String roomCode, RoomStatus roomStatus) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                getRoom(roomCode).setRoomStatus(roomStatus);
                return true;
            }
            return false;
        }
    }

    public boolean changePresenter(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).setPresenter(userID);
            }
            return false;
        }
    }

    public List<Person> getNotPresentedPeople(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getNotPresentedPeople();
            } else {
                return null;
            }
        }
    }

    public boolean setTargetInRoom(String roomCode, Target target) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                getRoom(roomCode).setTarget(target);
                return true;
            }
            return false;
        }
    }

    public boolean setGeoStatusInRoom(String roomCode, GeoguesserStatus status) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                getRoom(roomCode).setGeoStatus(status);
                return true;
            }
            return false;
        }
    }

    public List<GameType> availableGames(String roomCode, String userID, String fieldName) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getAvailableGames(userID, fieldName);
            }
            return null;
        }
    }

    public boolean addToPresentedList(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).addToPresentedList();
            }
            return false;
        }
    }

    public String getFieldValue(String roomCode, String userID, String fieldName) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).getFieldValue(userID, fieldName);
            }
            return null;
        }
    }

    public PresentRoomInfo getPresentRoomInfo(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.getPresentRoomInfo();
            }
            return null;
        }
    }

    public boolean setPresentRoomInfo(String roomCode, String field) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                PresentRoomInfo presentRoomInfo = room.getPresentRoomInfo();
                switch (field) {
                    case "firstName" -> presentRoomInfo.setFirstName(true);
                    case "lastName" -> presentRoomInfo.setLastName(true);
                    case "country" -> presentRoomInfo.setCountry(true);
                    case "city" -> presentRoomInfo.setCity(true);
                    case "feeling" -> presentRoomInfo.setFeeling(true);
                    case "favFood" -> presentRoomInfo.setFavFood(true);
                    case "favActivity" -> presentRoomInfo.setFavActivity(true);
                }
                room.setPresentRoomInfo(presentRoomInfo);
                return true;
            }
            return false;
        }
    }

    public GeoguesserStatus getGeoguesserStatus(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.getGeoStatus();
            }
            return null;
        }
    }

    public boolean setTargetLocation(String roomCode, String location, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.setLocation(location, userID);
            }
        }
        return false;
    }

    public boolean revealAllFields(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                room.setPresentRoomInfo(new PresentRoomInfo(
                        true, true, true, true, true, true, true
                ));
                return true;
            }
            return false;
        }
    }

    public boolean notifyPeople(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).notifyPeople(userID);
            }
            return false;
        }
    }

    public boolean checkNotSubmission(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.checkNotSubmitted(userID);
            }
            return true;
        }
    }

    public boolean isNotified(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return getRoom(roomCode).isNotified(userID);
            }
            return false;
        }
    }

    public boolean restartMockRoom() {
        synchronized (this) {
            destroyRoom("TEST");
            this.mockRoom = createMockRoom();
            return true;
        }
    }

    public void resetGuessedList(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                getRoom(roomCode).resetGuessedList();
            }
        }
    }

    public void addCorrectGuesser(String roomCode, String guesserId) {
        synchronized (this) {
            Room room = getRoom(roomCode);
            List<String> correctlyGuessedPlayers = room.getCorrectlyGuessedPlayerIds();
            if (correctlyGuessedPlayers.contains(guesserId)) return;
            correctlyGuessedPlayers.add(guesserId);
        }
    }

    public boolean allGuessed(String roomCode) {
        synchronized (this) {
            return getRoom(roomCode).allGuessed();
        }
    }

    public boolean forceBackToAllPresentedRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                room.setRoomStatus(RoomStatus.ALL_PRESENTED);
                return true;
            }
            return false;
        }
    }

    public boolean resetPresentRoomInfo(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                room.resetPresentRoomInfo();
                return true;
            }
            return false;
        }
    }

    public List<Person> geoGuesserWinner(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.geoGuesserWinner();
            }
            return null;
        }
    }

    public List<Person> geoGuesserPersonRank(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.geoGuesserPersonRank();
            }
            return null;
        }
    }

    public List<Double> geoGuesserDistanceRank(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.geoGuesserDistanceRank();
            }
            return null;
        }
    }

    public String presenterLocation(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.presenterLocation();
            }
            return "Room Not Found";
        }
    }

    public boolean resetGeoguesser(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                room.resetGeoguesser();
                return true;
            }
            return false;
        }
    }

    public boolean setField(String roomCode, String fieldName) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                room.setField(fieldName);
                return true;
            }
            return false;
        }
    }

    public String geoGuesserFieldName(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                Room room = getRoom(roomCode);
                return room.getGeoGuesserFieldName();
            }
            return "";
        }
    }

    public boolean startPresenting(String roomCode) {
        synchronized (this) {
            return serverStartRoom(roomCode);
        }
    }
}
