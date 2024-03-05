package com.icebreaker.controllers.httphandlers;

import com.icebreaker.person.Person;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import com.icebreaker.utils.JsonUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PersonHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    public PersonHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @GetMapping("/isAdmin")
    public boolean isAdmin(@RequestParam("userID") String userID, @RequestParam("roomCode") String roomCode) {
        try {
            return runner.isAdmin(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/isPresenter")
    public boolean isPresenter(@RequestParam("userID") String userID, @RequestParam("roomCode") String roomCode) {
        try {
            return runner.isPresenter(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping(path = "/updatePerson", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String updatePerson(@RequestBody Person person) {
        if (runner.roomUpdateUser(person)) {
            waitRoomService.broadcastMessage(person.getRoomCode());
            return "Success";
        } else {
            return "Fail";
        }
    }

    @DeleteMapping("/kickPerson")
    public boolean kickPerson(@RequestParam(name = "userID") String userID, @RequestParam(name = "roomCode") String roomCode) {
        boolean result = runner.kickPerson(roomCode, userID);
        if (result) {
            waitRoomService.broadcastMessage(roomCode);
        }
        return result;
    }

    @GetMapping("/getPlayers")
    public String getPlayersInARoom(@RequestParam(name = "roomCode") String roomCode) {
        if (runner.containsRoom(roomCode)) {
            Person admin = runner.getAdminInRoom(roomCode);
            Person presenter = runner.getPresenterInRoom(roomCode);
            RoomStatus status = runner.getStatus(roomCode);
            List<Person> otherPlayers = runner.getOtherPlayersInRoom(roomCode);
            return JsonUtils.returnJson(Map.of("admin", admin, "otherPlayers", otherPlayers, "presenter", presenter, "roomStatus", status), JsonUtils.unknownError);
        }
        return JsonUtils.returnRoomNotFoundJsonError();
    }

    @GetMapping("/getPlayer")
    public String getPlayerInARoom(@RequestParam(name = "roomCode") String roomCode, @RequestParam(name = "userID") String userID) {
        if (!runner.containsRoom(roomCode)) {
            return JsonUtils.returnRoomNotFoundJsonError();
        }
        Person person = runner.getOnePlayerInfo(roomCode, userID);
        if (person != null) {
            return JsonUtils.returnJson(Map.of("userInfo", person), JsonUtils.personNotFound);
        }
        return JsonUtils.returnPersonNotFoundJsonError();
    }

    @GetMapping("/getPresenter")
    public String getPresenterInARoom(@RequestParam(name = "roomCode") String roomCode) {
        if (!runner.containsRoom(roomCode)) {
            return JsonUtils.returnRoomNotFoundJsonError();
        }
        Person presenter = runner.getPresenterInRoom(roomCode);
        if (presenter != null) {
            return JsonUtils.returnJson(Map.of("presenter", presenter), JsonUtils.personNotFound);
        }
        return JsonUtils.returnPersonNotFoundJsonError();
    }

    @PostMapping("/pushNotification")
    public boolean pushNotification(@RequestParam(name = "roomCode") String roomCode, @RequestParam(name = "userID") String userID) {
        boolean result = runner.notifyPeople(roomCode, userID);
        if (result) {
            waitRoomService.broadcastMessage(roomCode);
        }
        return result;
    }

    @GetMapping("/isNotified")
    public boolean isNotified(@RequestParam(name = "roomCode") String roomCode, @RequestParam(name = "userID") String userID) {
        return runner.isNotified(roomCode, userID);
    }
}
