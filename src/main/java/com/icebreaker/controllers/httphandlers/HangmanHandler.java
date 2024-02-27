package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.WaitRoomService;
import com.icebreaker.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class HangmanHandler {
    private final HangmanService hangmanService;
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;
    @Autowired
    public HangmanHandler(HangmanService hangmanService, WaitRoomService waitRoomService) {
        this.hangmanService = hangmanService;
        this.waitRoomService = waitRoomService;
    }


    @PostMapping("/startHangman")
    public boolean startHangman(@RequestParam(name = "roomCode") String roomCode,
                                @RequestParam(name = "userID") String userID,
                                @RequestParam(name = "field") String field) {
        System.out.println("Start Hangman in room:" + roomCode + " User:" + userID + " Field:" + field);
        if (runner.changeRoomStatus(roomCode, RoomStatus.HANGMAN)) {
            String word = runner.getFieldValue(roomCode, userID, field);
            System.out.println("The hangman word is: " + word);
            boolean result = hangmanService.setAnswers(roomCode, word, field);
            waitRoomService.broadcastMessage(roomCode);
            return result;
        }
        return false;
    }

    @GetMapping("/getHangmanInfo")
    public Character[] getHangmanInfo(@RequestParam(name = "roomCode") String roomCode) {
        if (hangmanService.roomExist(roomCode)) {
            System.out.println("Get hangman info, the word is: " + hangmanService.getAnswer(roomCode) +
                    " With length: " + hangmanService.getAnswer(roomCode).getTargetWord().length());
            return hangmanService.getCurrentStages(roomCode); // .getAnswer(roomCode).getTargetWord().length();
        }
        return null;
    }

    @GetMapping("/getHangmanAnswer")
    public String getHangmanAnswer(@RequestParam(name = "roomCode") String roomCode) {
        if (hangmanService.roomExist(roomCode)) {
            System.out.println("Get hangman answer, the answer is: " + hangmanService.getAnswer(roomCode));
            Target target =  hangmanService.getAnswer(roomCode);
            return JsonUtils.returnJson(Map.of("target", target), "Room not found");
        }
        return "Error";
    }

}
