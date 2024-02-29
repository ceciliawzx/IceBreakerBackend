package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GeoguesserHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;
    public GeoguesserHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("startGeoguesser")
    public boolean startGeoguesser(@RequestParam(name = "roomCode", required = true) String roomCode) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.GEO_GUESSING)) {
            waitRoomService.broadcastMessage(roomCode);
            return true;
        }
        return false;
    }

    @GetMapping("getGeoguesserStatus")
    public String getGeoguesserStatus(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (runner.containsRoom(roomCode)) {
            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("status", runner.getGeoguesserStatus(roomCode)));
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }

            return json;
        }

        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", "Room not found"));
        } catch (Exception e) {
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}";
        }

        return jsonError;
    }

    @PostMapping("setTargetLocation")
    public boolean setTargetLocation(@RequestParam(name = "roomCode", required = true) String roomCode,
                                     @RequestParam(name = "location", required = true) String location,
                                     @RequestParam(name = "userID", required = true) String userID) {
        return runner.setTargetLocation(roomCode, location, userID);
    }

    @GetMapping("getUserGeoSubmission")
    public boolean getUserGeoSubmission(@RequestParam(name = "roomCode", required = true) String roomCode,
                                        @RequestParam(name = "userID", required = true) String userID) {
        return !runner.checkNotSubmission(roomCode, userID);
    }

    @GetMapping("/geoGuesserRank")
    public String geoGuesserRank(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (runner.containsRoom(roomCode)) {
            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("winner", runner.geoGuesserWinner(roomCode), "rankPerson", runner.geoGuesserPersonRank(roomCode), "rankDistance", runner.geoGuesserDistanceRank(roomCode)));
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }

            return json;
        }

        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", "Room not found"));
        } catch (Exception e) {
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}";
        }

        return jsonError;
    }

    @GetMapping("/presenterLocation")
    public String presenterLocation(@RequestParam(name = "roomCode", required = true) String roomCode) {
        return runner.presenterLocation(roomCode);
    }
}
