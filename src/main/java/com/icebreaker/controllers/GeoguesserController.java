package com.icebreaker.controllers;

import com.icebreaker.services.GeoguesserService;
import com.icebreaker.websocket.GeoguesserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class GeoguesserController {

    private final GeoguesserService geoguesserService;

    @Autowired
    public GeoguesserController(GeoguesserService geoguesserService) {
        this.geoguesserService = geoguesserService;
    }

    /* WebSocket */
    @MessageMapping("/room/{roomCode}/sendGuessing")
    public void handleMessage(@Payload GeoguesserMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        geoguesserService.broadcastGuessing(roomCode, message);
    }

    /* HTTP Handler */
    @PostMapping("/startGeoguesser")
    public boolean startGeoguesser(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "fieldName") String fieldName) {
        return geoguesserService.startGeoguesser(roomCode, fieldName);
    }

    @GetMapping("/getGeoguesserStatus")
    public String getGeoguesserStatus(@RequestParam(name = "roomCode") String roomCode) {
        return geoguesserService.getGeoguesserStatus(roomCode);
    }

    @PostMapping("/setTargetLocation")
    public boolean setTargetLocation(@RequestParam(name = "roomCode") String roomCode,
                                     @RequestParam(name = "location") String location,
                                     @RequestParam(name = "userID") String userID) {
        return geoguesserService.setTargetLocation(roomCode, location, userID);
    }

    @GetMapping("/getUserGeoSubmission")
    public boolean getUserGeoSubmission(@RequestParam(name = "roomCode") String roomCode,
                                        @RequestParam(name = "userID") String userID) {
        return geoguesserService.getUserGeoSubmission(roomCode, userID);
    }

    @GetMapping("/geoGuesserRank")
    public String geoGuesserRank(@RequestParam(name = "roomCode") String roomCode) {
        return geoguesserService.geoGuesserRank(roomCode);
    }

    @GetMapping("/presenterLocation")
    public String presenterLocation(@RequestParam(name = "roomCode") String roomCode) {
        return geoguesserService.presenterLocation(roomCode);
    }

    @GetMapping("/geoguesserFieldName")
    public String geoguesserFieldName(@RequestParam(name = "roomCode") String roomCode) {
        return geoguesserService.geoguesserFieldName(roomCode);
    }

    @PostMapping("/geoguesserForceEnd")
    public boolean geoguesserForceEnd(@RequestParam(name = "roomCode") String roomCode) {
        return geoguesserService.geoguesserForceEnd(roomCode);
    }
}
