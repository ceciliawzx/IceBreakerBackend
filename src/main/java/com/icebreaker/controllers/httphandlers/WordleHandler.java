package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WordleHandler {
    private final WordleService wordleService;
    private final ServerRunner runner = ServerRunner.getInstance();
    @Autowired
    public WordleHandler(WordleService wordleService) {
        this.wordleService = wordleService;
    }

    @PostMapping("/startWordle")
    public boolean startWordle(@RequestParam(name = "roomCode", required = true) String roomCode,
                               @RequestParam(name = "userID", required = true) String userID,
                               @RequestParam(name = "field", required = true) String field) {
        System.out.println("Start Wordle in room:" + roomCode + " User:" + userID + " Field:" + field);
        if (runner.changeRoomStatus(roomCode, RoomStatus.WORDLING)) {
            String word = runner.getFieldValue(roomCode, userID, field);
            System.out.println("The wordle word is: " + word);
            return wordleService.setAnswers(roomCode, word);
        }
        return false;
    }

    @GetMapping("/getWordleInfo")
    public int getWordleInfo(@RequestParam(name = "roomCode", required = true) String roomCode) {
        if (wordleService.roomExist(roomCode)) {
            System.out.println("Get wordle info, the word is: " + wordleService.getAnswer(roomCode) +
                    " With length: " + wordleService.getAnswer(roomCode).length());
            return wordleService.getAnswer(roomCode).length();
        }
        return -1;
    }

    @GetMapping("/getWordleAnswer")
    public String getWordleAnswer(@RequestParam(name = "roomCode", required = true) String roomCode) {
        if (wordleService.roomExist(roomCode)) {
            System.out.println("Get wordle answer, the answer is: " + wordleService.getAnswer(roomCode));
            return wordleService.getAnswer(roomCode);
        }
        return "Error";
    }
}
