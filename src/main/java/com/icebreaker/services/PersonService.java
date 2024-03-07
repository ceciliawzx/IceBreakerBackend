package com.icebreaker.services;

import com.icebreaker.dto.person.Person;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    @Autowired
    public PersonService(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }


    public boolean isAdmin(String userID, String roomCode) {
        try {
            return runner.isAdmin(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPresenter(String userID, String roomCode) {
        try {
            return runner.isPresenter(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String updatePerson(Person person) {
        if (runner.roomUpdateUser(person)) {
            waitRoomService.broadcastMessage(person.getRoomCode());
            return "Success";
        } else {
            return "Fail";
        }
    }

    public boolean kickPerson(String roomCode, String userID) {
        boolean result = runner.kickPerson(roomCode, userID);
        if (result) {
            waitRoomService.broadcastMessage(roomCode);
        }
        return result;
    }

    public boolean isNotified(String roomCode, String userID) {
        return runner.isNotified(roomCode, userID);
    }

}
