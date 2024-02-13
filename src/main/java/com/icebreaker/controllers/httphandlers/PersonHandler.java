package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.person.Person;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PersonHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @GetMapping("/isAdmin")
    public boolean isAdmin(@RequestParam("userID") String userID,
                           @RequestParam("roomCode") String roomCode) {
//        System.out.printf("Check Admin, User: %s, Room: %s%n", userID, roomCode);
        try {
            return runner.isAdmin(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/isPresenter")
    public boolean isPresenter(@RequestParam("userID") String userID,
                               @RequestParam("roomCode") String roomCode) {
//        System.out.printf("Check Presenter, User: %s, Room: %s%n", userID, roomCode);

        try {
            return runner.isPresenter(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping(path = "/updatePerson", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String updatePerson(@RequestBody Person person) {
        System.out.printf("Update Person, User: %s, Room: %s%n", person.getUserID(), person.getRoomCode());
        if (runner.roomUpdateUser(person)) {
            return "Success";
        } else {
            return "Fail";
        }
    }

    @DeleteMapping("/kickPerson")
    public boolean kickPerson(@RequestParam(name = "userID") String userID,
                              @RequestParam(name = "roomCode") String roomCode) {
        System.out.printf("Kick Person: %s, In room: %s%n", userID, roomCode);
        return runner.kickPerson(roomCode, userID);
    }


    @GetMapping("/getPlayers")
    public String getPlayersInARoom(@RequestParam(name = "roomCode") String roomCode) {
//        System.out.println("Get players in room: " + roomCode);
        if (runner.containsRoom(roomCode)) {
            Person admin = runner.getAdminInRoom(roomCode);
            Person presenter = runner.getPresenterInRoom(roomCode);
            RoomStatus status = runner.getStatus(roomCode);
            List<Person> otherPlayers = runner.getOtherPlayersInRoom(roomCode);
            return JsonUtils.returnJson(Map.of("admin", admin, "otherPlayers", otherPlayers, "presenter", presenter, "roomStatus", status), "Room not found");
        }
        return null;
    }

    @GetMapping("/getPlayer")
    public String getPlayerInARoom(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "userID") String userID) {
//        System.out.println("Ger player: " + userID + " in Room: " + roomCode);
        ObjectMapper objectMapper = new ObjectMapper();
        if (!runner.containsRoom(roomCode)) {
            String jsonRoomError;
            try {
                jsonRoomError = objectMapper.writeValueAsString(Map.of("error", "Room Not Found"));
            } catch (Exception e) {
                e.printStackTrace();
                jsonRoomError = "{\"error\": \"Serialization error\"}";
            }
            return jsonRoomError;
        }
        Person person = runner.getOnePlayerInfo(roomCode, userID);
        if (person != null) {
            String json;
            try {
                json = objectMapper.writeValueAsString(Map.of("userInfo", person));
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }
            return json;
        }

        String jsonPersonError;
        try {
            jsonPersonError = objectMapper.writeValueAsString(Map.of("error", "Person Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            jsonPersonError = "{\"error\": \"Serialization error\"}";
        }
        return jsonPersonError;
    }
}
