package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.room.PresentRoomInfo;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import com.icebreaker.services.WordleService;
import com.icebreaker.utils.JsonUtils;
import com.icebreaker.websocket.WordleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class WordleHandler {
    private final WordleService wordleService;
    private final WaitRoomService waitRoomService;
    private final ServerRunner runner = ServerRunner.getInstance();
    @Autowired
    public WordleHandler(WordleService wordleService, WaitRoomService waitRoomService) {
        this.wordleService = wordleService;
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("/startWordle")
    public boolean startWordle(@RequestParam(name = "roomCode") String roomCode,
                               @RequestParam(name = "userID") String userID,
                               @RequestParam(name = "field") String field) {
        System.out.println("Start Wordle in room:" + roomCode + " User:" + userID + " Field:" + field);
        if (runner.changeRoomStatus(roomCode, RoomStatus.WORDLING)) {
            String word = runner.getFieldValue(roomCode, userID, field);
            System.out.println("The wordle word is: " + word);
            boolean result = wordleService.setAnswers(roomCode, field, word);
            waitRoomService.broadcastMessage(roomCode);
            return result;
        }
        return false;
    }

    @GetMapping("/getWordleInfo")
    public int getWordleInfo(@RequestParam(name = "roomCode") String roomCode) {
        if (wordleService.roomExist(roomCode)) {
            System.out.println("Get wordle info, the word is: " + wordleService.getAnswer(roomCode) +
                    " With length: " + wordleService.getAnswer(roomCode).getTargetWord().length());
            return wordleService.getAnswer(roomCode).getTargetWord().length();
        }
        return -1;
    }

    @GetMapping("/getWordleAnswer")
    public String getWordleAnswer(@RequestParam(name = "roomCode") String roomCode) {
        if (wordleService.roomExist(roomCode)) {
            System.out.println("Get wordle answer, the answer is: " + wordleService.getAnswer(roomCode));
            Target target = wordleService.getAnswer(roomCode);
            return JsonUtils.returnJson(Map.of("target", target), "Error fetching Wordle answer");
        }
        return "Error";
    }

    @GetMapping("/getWordleGameStatus")
    public String getWordleGameStatus(@RequestParam(name = "roomCode") String roomCode) {
        if (wordleService.roomExist(roomCode)) {
            try {
                return JsonUtils.returnJson(Map.of("wordlemessage", wordleService.getGameStatus(roomCode)), "Error fetching Wordle Status");
            } catch (Exception e) {

            }
        }
        return "Error";
    }
}
