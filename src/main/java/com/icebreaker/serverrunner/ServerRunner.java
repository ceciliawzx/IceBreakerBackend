package com.icebreaker.serverrunner;

import com.icebreaker.room.Room;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

public class ServerRunner {
    private static ServerRunner instance;

    private List<Room> activeRooms = new ArrayList<Room>();

    private ServerRunner() {}

    public static ServerRunner getInstance() {
        if (instance == null) {
            instance = new ServerRunner();
        }
        return instance;
    }

    public boolean addRoom(Room room) {
        activeRooms.add(room);
        return true;
    }

    public boolean destroyRoom(int roomNumber) {
        for (Room r : activeRooms) {
            if (roomNumber == r.getRoomNumber()) {
                activeRooms.remove(r);
                return true;
            }
        }
        return false;
    }

    public boolean joinRoom(int roomNumber, HttpServletRequest request) {
        for (Room r : activeRooms) {
            if (roomNumber == r.getRoomNumber()) {
                r.joinRoom(request);
                return true;
            }
        }
        return false;
    }
}
