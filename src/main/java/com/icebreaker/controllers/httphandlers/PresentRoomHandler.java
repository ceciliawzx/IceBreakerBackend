package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.room.PresentRoomInfo;
import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.DrawingService;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PresentRoomHandler {
    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final DrawingService drawingService;
    private final ServerRunner runner = ServerRunner.getInstance();

    @Autowired
    public PresentRoomHandler(WordleService wordleService, HangmanService hangmanService, DrawingService drawingService) {
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
        this.drawingService = drawingService;
    }

    @GetMapping("/getPresentRoomInfo")
    public String getPresentRoomInfo(@RequestParam(name = "roomCode", required = true) String roomCode) {
        System.out.println("Get Present Room Info, Room Code: " + roomCode);
        ObjectMapper objectMapper = new ObjectMapper();
        PresentRoomInfo presentRoomInfo =  runner.getPresentRoomInfo(roomCode);
        if (presentRoomInfo != null) {
            String json;
            try {
                json = objectMapper.writeValueAsString(Map.of("presentRoomInfo", presentRoomInfo));
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }
            return json;
        }
        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", "error fetching presentRoomInfo"));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}";
        }
        return jsonError;
    }

    @PostMapping("setPresentRoomInfo")
    public boolean setPresentRoomInfo(@RequestParam(name = "roomCode", required = true) String roomCode,
                                      @RequestParam(name = "field", required = true) String field) {
        Room room = runner.getRoom(roomCode);
        PresentRoomInfo presentRoomInfo = room.getPresentRoomInfo();
        switch (field) {
            case "firstName" -> presentRoomInfo.setFirstName(true);
            case "lastName" -> presentRoomInfo.setLastName(true);
            case "country" -> presentRoomInfo.setCountry(true);
            case "city" -> presentRoomInfo.setCity(true);
            case "feeling" -> presentRoomInfo.setFeeling(true);
            case "favFood" -> presentRoomInfo.setFavFood(true);
            case "favActivity" -> presentRoomInfo.setFavActivity(true);
        }
        System.out.println("SetPresentRoomInfo in room " + roomCode + " receives ");
        return runner.setPresentRoomInfo(roomCode, presentRoomInfo);
    }

    @PostMapping("/backToPresentRoom")
    public String backToPresentRoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        RoomStatus currentStat = runner.getStatus(roomCode);
        System.out.println("Back To Presenting Room, curStat " + currentStat);
        if (runner.changeRoomStatus(roomCode, RoomStatus.PRESENTING)) {
            runner.setTargetInRoom(roomCode, "");
            if (currentStat == RoomStatus.WORDLING) {
                wordleService.returnToPresentingRoom(roomCode);
                wordleService.resetSession(roomCode);
                System.out.println("Reseting Wordle");
            } else if (currentStat == RoomStatus.HANGMAN) {
                hangmanService.returnToPresentingRoom(roomCode);
                hangmanService.resetSession(roomCode);
                System.out.println("Reseting Hangman");
            } else if (currentStat == RoomStatus.PICTURING) {
                drawingService.returnToPresentingRoom(roomCode);
                System.out.println("Reseting Pictionary");
            }
            return "Success";
        }
        return "Fail";
    }

}
