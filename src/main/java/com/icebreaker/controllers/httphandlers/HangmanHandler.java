package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.HangmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HangmanHandler {
    private final HangmanService hangmanService;
    private final ServerRunner runner = ServerRunner.getInstance();
    @Autowired
    public HangmanHandler(HangmanService hangmanService) {
        this.hangmanService = hangmanService;
    }


    @PostMapping("/startHangman")
    public boolean startHangman(@RequestParam(name = "roomCode", required = true) String roomCode,
                                @RequestParam(name = "userID", required = true) String userID,
                                @RequestParam(name = "field", required = true) String field) {
        System.out.println("Start Hangman in room:" + roomCode + " User:" + userID + " Field:" + field);
        if (runner.changeRoomStatus(roomCode, RoomStatus.HANGMAN)) {
            String word = runner.getFieldValue(roomCode, userID, field);
            System.out.println("The hangman word is: " + word);
            return hangmanService.setAnswers(roomCode, word);
        }
        return false;
    }

    @GetMapping("/getHangmanInfo")
    public int getHangmanInfo(@RequestParam(name = "roomCode", required = true) String roomCode) {
        if (hangmanService.roomExist(roomCode)) {
            System.out.println("Get hangman info, the word is: " + hangmanService.getAnswer(roomCode) +
                    " With length: " + hangmanService.getAnswer(roomCode).length());
            return hangmanService.getAnswer(roomCode).length();
        }
        return -1;
    }

    @GetMapping("/getHangmanAnswer")
    public String getHangmanAnswer(@RequestParam(name = "roomCode", required = true) String roomCode) {
        if (hangmanService.roomExist(roomCode)) {
            System.out.println("Get hangman answer, the answer is: " + hangmanService.getAnswer(roomCode));
            return hangmanService.getAnswer(roomCode);
        }
        return "Error";
    }
}
