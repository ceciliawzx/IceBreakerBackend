package com.icebreaker.serverrunner;

import com.icebreaker.room.Room;
import jakarta.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ServerRunner {
    private static ServerRunner instance;

    private final Map<Room, Integer> activeRooms = new HashMap<>();
    private final Map<Integer, Room> roomNumbers = new HashMap<>();

//    private final List<Room> activeRooms = new ArrayList<>();

    private ServerRunner() {}

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

    public boolean addRoom(Room room) {
        activeRooms.put(room, room.getRoomNumber());
        roomNumbers.put(room.getRoomNumber(), room);
        return true;
    }

    public boolean destroyRoom(int roomNumber) {
//        for (Room r : activeRooms) {
//            if (roomNumber == r.getRoomNumber()) {
//                activeRooms.remove(r);
//                return true;
//            }
//        }
        if (this.containsRoom(roomNumber)) {
            activeRooms.remove(roomNumbers.get(roomNumber));
            roomNumbers.remove(roomNumber);
            return true;
        }
        return false;
    }

    public boolean joinRoom(int roomNumber, HttpServletRequest request) {
//        for (Room r : activeRooms) {
//            if (roomNumber == r.getRoomNumber()) {
//                r.joinRoom(request);
//                return true;
//            }
//        }
        if (this.containsRoom(roomNumber)) {
            roomNumbers.get(roomNumber).joinRoom(request);
            return true;
        }
        return false;
    }
}
