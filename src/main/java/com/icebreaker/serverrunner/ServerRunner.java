package com.icebreaker.serverrunner;

import com.icebreaker.person.Person;
import com.icebreaker.person.User;
import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.utils.RoomCodeGenerator;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerRunner {
    private static ServerRunner instance;
    @Getter
    private final RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();

    private final Map<Room, String> activeRooms = new HashMap<>();
    private final Map<String, Room> roomNumbers = new HashMap<>();
    private final Map<String, String> codeNumberMapping = new HashMap<>();
    private final Map<String, String> numberCodeMapping = new HashMap<>();

    private ServerRunner() {
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
            return codeNumberMapping.containsKey(roomCode) || numberCodeMapping.containsKey(roomCode);
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

    public boolean destroyRoom(String roomCode, boolean isRoomCode) {
        synchronized (this) {
            if (this.containsRoom(roomCode)) {
                if (isRoomCode) {
                    // room code
                    String roomNumber = codeNumberMapping.get(roomCode);
                    activeRooms.remove(roomNumbers.get(roomNumber));
                    roomNumbers.remove(roomNumber);
                    codeNumberMapping.remove(roomCode);
                    numberCodeMapping.remove(roomNumber);
                    roomCodeGenerator.deleteUnUseCode(roomCode);
                } else {
                    // room number
                    activeRooms.remove(roomNumbers.get(roomCode));
                    roomNumbers.remove(roomCode);
                    codeNumberMapping.remove(numberCodeMapping.get(roomCode), roomCode);
                    numberCodeMapping.remove(roomCode);
                }
                return true;
            } else {
                return false;
            }

        }
    }


    public boolean joinRoom(String roomCode, String nickname, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNumber = codeNumberMapping.get(roomCode);
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
                String roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getPlayers();
            }
            return null;
        }
    }

    public Person getPresenterInRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getPresenter();
            }
            return null;
        }
    }

    public Person getOnePlayerInfo(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).getPlayer(userID);
            }
            return null;
        }
    }

    public boolean isAdmin(String userID, String roomCode) throws Exception {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNum = this.codeNumberMapping.get(roomCode);
                Room room = roomNumbers.get(roomNum);
                return userID.equals(room.getHost().getUserID());
            }
            return false;
        }
    }

    public boolean isPresenter(String userID, String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNum = this.codeNumberMapping.get(roomCode);
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
                String roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).kickPerson(userID);
            }
            return false;
        }
    }

    public boolean serverStartRoom(String roomCode) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNumber = codeNumberMapping.get(roomCode);
                roomNumbers.get(roomNumber).startRoom();
                return true;
            }
            return false;
        }
    }

    public boolean checkPlayerInfoComplete(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).checkPlayerInfoComplete(userID);
            }
            return false;
        }
    }

    public boolean changeRoomStatus(String roomCode, RoomStatus roomStatus) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNumber = codeNumberMapping.get(roomCode);
                roomNumbers.get(roomNumber).setRoomStatus(roomStatus);
                return true;
            }
            return false;
        }
    }

    public boolean changePresenter(String roomCode, String userID) {
        synchronized (this) {
            if (containsRoom(roomCode)) {
                String roomNumber = codeNumberMapping.get(roomCode);
                return roomNumbers.get(roomNumber).setPresenter(userID);
            }
            return false;
        }
    }
}
