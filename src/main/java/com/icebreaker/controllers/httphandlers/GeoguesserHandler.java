package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.GeoguesserService;
import com.icebreaker.services.WaitRoomService;
import com.icebreaker.utils.JsonUtils;
import com.icebreaker.websocket.GeoguesserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GeoguesserHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;
    private final GeoguesserService geoguesserService;

    public GeoguesserHandler(WaitRoomService waitRoomService, GeoguesserService geoguesserService) {
        this.waitRoomService = waitRoomService;
        this.geoguesserService = geoguesserService;
    }

    @PostMapping("startGeoguesser")
    public boolean startGeoguesser(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "fieldName") String fieldName) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.GEO_GUESSING)) {
            runner.resetGeoguesser(roomCode);
            runner.setField(roomCode, fieldName);
            waitRoomService.broadcastMessage(roomCode);
            return true;
        }
        return false;
    }

    @GetMapping("getGeoguesserStatus")
    public String getGeoguesserStatus(@RequestParam(name = "roomCode") String roomCode) {
        if (runner.containsRoom(roomCode)) {
            JsonUtils.returnJson(Map.of("status", runner.getGeoguesserStatus(roomCode)), JsonUtils.returnJsonError("Serilisation error"));
        }
        return JsonUtils.roomNotFound;
    }

    @PostMapping("setTargetLocation")
    public boolean setTargetLocation(@RequestParam(name = "roomCode") String roomCode,
                                     @RequestParam(name = "location") String location,
                                     @RequestParam(name = "userID") String userID) {
        boolean isSet = runner.setTargetLocation(roomCode, location, userID);
        waitRoomService.broadcastMessage(roomCode);
        return isSet;
    }

    @GetMapping("getUserGeoSubmission")
    public boolean getUserGeoSubmission(@RequestParam(name = "roomCode") String roomCode,
                                        @RequestParam(name = "userID") String userID) {
        return !runner.checkNotSubmission(roomCode, userID);
    }

    @GetMapping("/geoGuesserRank")
    public String geoGuesserRank(@RequestParam(name = "roomCode") String roomCode) {
        if (runner.containsRoom(roomCode)) {
            return JsonUtils.returnJson(Map.of("winner", runner.geoGuesserWinner(roomCode), "rankPerson", runner.geoGuesserPersonRank(roomCode), "rankDistance", runner.geoGuesserDistanceRank(roomCode)), JsonUtils.returnJsonError("Serialisation error"));
        }
        return JsonUtils.roomNotFound;
    }

    @GetMapping("/presenterLocation")
    public String presenterLocation(@RequestParam(name = "roomCode") String roomCode) {
        return runner.presenterLocation(roomCode);
    }

    @GetMapping("/geoguesserFieldName")
    public String geoguesserFieldName(@RequestParam(name = "roomCode") String roomCode) {
        return runner.geoGuesserFieldName(roomCode);
    }

}
