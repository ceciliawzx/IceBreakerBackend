package com.icebreaker.serverrunner;

import com.icebreaker.person.Admin;
import com.icebreaker.person.Person;
import com.icebreaker.person.User;
import com.icebreaker.room.*;
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
    private final int mockRoomNumber = -1;
    private final String mockRoomCode = "TEST";
    private Room mockRoom;
    private final Person mockAlex = new User("Alexy", mockRoomCode, "2", Constants.getYellowDuck(),
            "Alex", "Yang", "China", "Shanghai", "Sleepy",
            "Sweet and sour chicken", "Sleep", true);
    private final Person mockMohammed = new User("Moha", mockRoomCode, "3", Constants.getYellowDuck(),
            "Mohammed", "Lee", "Syria", "Damascus", "Excited",
            "Baked Potato", "Fight", true);
    private final Person mockYHB = new User("Andersuki", mockRoomCode, "4", Constants.getYellowDuck(),
            "Yu", "HongBo", "China", "Harbin", "Tired",
            "Steak", "Gaming", true);
    private final Person mockWSY = new User("SelinaWan666", mockRoomCode, "5", Constants.getYellowDuck(),
            "Wan", "Siyu", "Maldives", "Olhuveli", "Happy",
            "Nang", "Sing", true);
    private final Admin mockAdminBob = new Admin("Bobby", mockRoomCode, "1", Constants.getYellowDuck(),
            "Bob", "Li", "China", "Beijing", "Sad",
            "Steak", "Travel", true);
    private final Map<String, Person> mockRoomUserIDMap = new HashMap<>();

    private ServerRunner() {
        this.mockRoom = createMockRoom();
        mockRoomUserIDMap.put("2", mockAlex);
        mockRoomUserIDMap.put("3", mockMohammed);
        mockRoomUserIDMap.put("4", mockYHB);
        mockRoomUserIDMap.put("5", mockWSY);
    }

    private Room createMockRoom() {
        Room mockRoom = new Room(mockRoomNumber, mockRoomCode, mockAdminBob);
        addRoom(mockRoom, mockRoomCode);
        joinRoom(mockRoomCode, "Alexy", "2");
        joinRoom(mockRoomCode, "Moha", "3");
        joinRoom(mockRoomCode, "Andersuki", "4");
        joinRoom(mockRoomCode, "SelinaWan666", "5");
        roomUpdateUser(mockAlex);
        roomUpdateUser(mockMohammed);
        roomUpdateUser(mockYHB);
        roomUpdateUser(mockWSY);
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
                getRoom(roomCode).joinRoom(new User(nickname, roomCode, userID));
                return true;
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
            }
            return null;
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

    public List<GameType> availableGames(String roomCode, String userID, String fieldName) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
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



    public boolean restartMockRoom() {
        destroyRoom("TEST");
        this.mockRoom = createMockRoom();
        return true;
    }
}
