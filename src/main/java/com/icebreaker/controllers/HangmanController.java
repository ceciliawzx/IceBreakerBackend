package com.icebreaker.controllers;

import com.icebreaker.services.HangmanService;
import com.icebreaker.websocket.HangmanMessage;
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
public class HangmanController {

    private final HangmanService hangmanService;

    @Autowired
    public HangmanController(HangmanService hangmanService) {
        this.hangmanService = hangmanService;
    }

    /* WebSocket */
    @MessageMapping("/room/{roomCode}/hangman")
    public void handleMessage(@Payload HangmanMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        hangmanService.broadcastResult(roomCode, message);
    }

    /* HTTP Handler */
    @PostMapping("/startHangman")
    public boolean startHangman(@RequestParam(name = "roomCode") String roomCode,
                                @RequestParam(name = "userID") String userID,
                                @RequestParam(name = "field") String field) {
        return hangmanService.startHangman(roomCode, userID, field);
    }

    @GetMapping("/getHangmanInfo")
    public Character[] getHangmanInfo(@RequestParam(name = "roomCode") String roomCode) {
        return hangmanService.getHangmanInfo(roomCode);
    }

    @GetMapping("/getHangmanAnswer")
    public String getHangmanAnswer(@RequestParam(name = "roomCode") String roomCode) {
        return hangmanService.getHangmanAnswer(roomCode);
    }

    @GetMapping("/getHangmanGameStatus")
    public String getHangmanGameStatus(@RequestParam(name = "roomCode") String roomCode) {
        return hangmanService.getHangmanGameStatus(roomCode);
    }
}
