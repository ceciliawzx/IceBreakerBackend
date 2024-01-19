package com.icebreaker.serverrunner;

import com.icebreaker.person.User;
import com.icebreaker.room.Room;
import com.icebreaker.utils.RoomCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ServerRunner {
    private static ServerRunner instance;
    @Getter
    private RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();

    private final Map<Room, Integer> activeRooms = new HashMap<>();
    private final Map<Integer, Room> roomNumbers = new HashMap<>();
    private final Map<String, Integer> codeNumberMapping  = new HashMap<>();
    private final Map<Integer, String> numberCodeMapping  = new HashMap<>();

    private ServerRunner() {
    }

    public static ServerRunner getInstance() {
        if (instance == null) {
            instance = new ServerRunner();
        }
        return instance;
    }

    public boolean containsRoom(Room room) {
        return activeRooms.containsKey(room);
    }

    public boolean containsRoom(int roomNumber) {
        return roomNumbers.containsKey(roomNumber);
    }

    public boolean addRoom(Room room, String code) {
        activeRooms.put(room, room.getRoomNumber());
        roomNumbers.put(room.getRoomNumber(), room);
        codeNumberMapping.put(code, room.getRoomNumber());
        numberCodeMapping.put(room.getRoomNumber(), code);
        return true;
    }

    public boolean destroyRoom(int roomNumber) {
        if (this.containsRoom(roomNumber)) {
            activeRooms.remove(roomNumbers.get(roomNumber));
            roomNumbers.remove(roomNumber);
            codeNumberMapping.remove(numberCodeMapping.get(roomNumber), roomNumber);
            numberCodeMapping.remove(roomNumber);
            return true;
        }
        return false;
    }

    public boolean joinRoom(String roomCode, String userID) {
        if (this.codeNumberMapping.containsKey(roomCode)) {
            int roomNumber = codeNumberMapping.get(roomCode);
            if (this.containsRoom(roomNumber)) {
                roomNumbers.get(roomNumber).joinRoom(userID);
                return true;
            }
        }
        return false;
    }

    public void roomAddUser(User user) {
        roomNumbers.get(user.getRoomId()).addUser(user);
    }

    public boolean isAdmin(String userID, String roomCode) {
        int roomNum = this.codeNumberMapping.get(roomCode);
        Room room = roomNumbers.get(roomNum);
        return userID.equals(room.getHost().getId());
    }
}
