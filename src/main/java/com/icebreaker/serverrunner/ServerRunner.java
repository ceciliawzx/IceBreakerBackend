package com.icebreaker.serverrunner;

import com.icebreaker.person.Admin;
import com.icebreaker.person.Person;
import com.icebreaker.person.User;
import com.icebreaker.room.GameType;
import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
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

    private ServerRunner() {
        int mockRoomNumber = -1;
        String mockRoomCode = "TEST";
        Admin mockAdminBob = new Admin("Bobby", mockRoomCode, "1", Constants.getYellowDuck(),
                "Bob", "Li", "China", "Beijing", "Sad",
                "Steak", "Travel", true);
        Room mockRoom = new Room(mockRoomNumber, mockRoomCode, mockAdminBob);
        addRoom(mockRoom, mockRoomCode);
        Person mockAlex = new User("Alexy", mockRoomCode, "2", Constants.getYellowDuck(),
                "Alex", "Yang", "China", "Shanghai", "Sleepy",
                "Sweet and sour chicken", "Sleep", true);
        Person mockMohammed = new User("Moha", mockRoomCode, "3", Constants.getYellowDuck(),
                "Mohammed", "Lee", "Syria", "Damascus", "Excited",
                "Baked Potato", "Fight", true);
        Person mockYHB = new User("Andersuki", mockRoomCode, "4", Constants.getYellowDuck(),
                "Yu", "HongBo", "China", "Harbin", "Tired",
                "Steak", "Gaming", true);
        Person mockWSY = new User("SelinaWan666", mockRoomCode, "5", Constants.getYellowDuck(),
                "Wan", "Siyu", "Maldives", "Olhuveli", "Happy",
                "Nang", "Sing", true);
        joinRoom(mockRoomCode, "Alexy", "2");
        joinRoom(mockRoomCode, "Moha", "3");
        joinRoom(mockRoomCode, "Andersuki", "4");
        joinRoom(mockRoomCode, "SelinaWan666", "5");
        roomUpdateUser(mockAlex);
        roomUpdateUser(mockMohammed);
        roomUpdateUser(mockYHB);
        roomUpdateUser(mockWSY);
    }

    public static ServerRunner getInstance() {

        if (instance == null) {
            instance = new ServerRunner();
        }
        return instance;
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
                int roomNumber = codeNumberMapping.get(roomCode);
                roomNumbers.get(roomNumber).joinRoom(new User(nickname, roomCode, userID));
                return true;
            }
            return false;
        }
    }


    public boolean roomUpdateUser(Person person) {
        synchronized (this) {
            if (containsRoom(person.getRoomCode())) {
                return roomNumbers.get(codeNumberMapping.get(person.getRoomCode())).updateUser(person);
            }
            return false;
        }
    }

    public List<Person> getPlayersInRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getPlayers();
            }
            return null;
        }
    }

    public Admin getAdminInRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getHost();
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
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getPresenter();
            }
            return null;
        }
    }

    public Person getOnePlayerInfo(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getPlayer(userID);
            }
            return null;
        }
    }

    public boolean isAdmin(String userID, String roomCode) throws Exception {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNum = this.codeNumberMapping.get(roomCode);
                Room room = roomNumbers.get(roomNum);
                return userID.equals(room.getHost().getUserID());
            }
            return false;
        }
    }

    public boolean isPresenter(String userID, String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNum = this.codeNumberMapping.get(roomCode);
                Room room = roomNumbers.get(roomNum);
                return userID.equals(room.getPresenter().getUserID());
            }
            return false;
        }
    }

    public RoomStatus getStatus(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                return roomNumbers.get(codeNumberMapping.get(roomCode)).getRoomStatus();
            }
            return RoomStatus.NON_EXIST;
        }
    }


    public boolean kickPerson(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).kickPerson(userID);
            }
            return false;
        }
    }

    public boolean serverStartRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                roomNumbers.get(roomNumber).startRoom();
                return true;
            }
            return false;
        }
    }

    public boolean checkPlayerInfoComplete(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).checkPlayerInfoComplete(userID);
            }
            return false;
        }
    }

    public boolean changeRoomStatus(String roomCode, RoomStatus roomStatus) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                roomNumbers.get(roomNumber).setRoomStatus(roomStatus);
                return true;
            }
            return false;
        }
    }

    public boolean changePresenter(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).setPresenter(userID);
            }
            return false;
        }
    }

    public List<Person> getNotPresentedPeople(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getNotPresentedPeople();
            }
            return null;
        }
    }

    public boolean setTargetInRoom(String roomCode, String target) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                roomNumbers.get(roomNumber).setTarget(target);
                return true;
            }
            return false;
        }
    }

    public List<GameType> availableGames(String roomCode, String userID, String fieldName) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getAvailableGames(userID, fieldName);
            }
            return null;
        }
    }

    public String getFieldValue(String roomCode, String userID, String fieldName) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                int roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getFieldValue(userID, fieldName);
            }
            return null;
        }
    }
}
