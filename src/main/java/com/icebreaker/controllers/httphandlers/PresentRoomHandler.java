package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.room.PresentRoomInfo;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
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
    private final ServerRunner runner = ServerRunner.getInstance();

    @Autowired
    public PresentRoomHandler(WordleService wordleService, HangmanService hangmanService) {
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
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

    @PostMapping(path="/setPresentRoomInfo", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean setPresentRoomInfo(@RequestParam(name = "roomCode", required = true) String roomCode,
                                      @RequestBody PresentRoomInfo presentRoomInfo) {

        System.out.println("SetPresentRoomInfo in room " + roomCode + " receives " + presentRoomInfo);
        return runner.setPresentRoomInfo(roomCode, presentRoomInfo);
    }

    @PostMapping("/backToPresentRoom")
    public String backToPresentRoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        System.out.println("Back To Presenting Room");
        RoomStatus currentStat = runner.getStatus(roomCode);
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
            }
            return "Success";
        }
        return "Fail";
    }
}
