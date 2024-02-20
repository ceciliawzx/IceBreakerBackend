package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.PresentRoomInfo;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.DrawingService;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.TimerService;
import com.icebreaker.services.WordleService;
import com.icebreaker.utils.JsonUtils;
import com.icebreaker.websocket.TimerMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class PresentRoomHandler {
    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final DrawingService drawingService;
    private final TimerService timerService;
    private final ServerRunner runner = ServerRunner.getInstance();

    @Autowired
    public PresentRoomHandler(WordleService wordleService, HangmanService hangmanService, DrawingService drawingService, TimerService timerService) {
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
        this.drawingService = drawingService;
        this.timerService = timerService;
    }

    @GetMapping("/getPresentRoomInfo")
    public String getPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode) {
//        System.out.println("Get Present Room Info, Room Code: " + roomCode);
        PresentRoomInfo presentRoomInfo = runner.getPresentRoomInfo(roomCode);
        return JsonUtils.returnJson(Map.of("presentRoomInfo", presentRoomInfo), "Room not found");
    }

    @PostMapping("setPresentRoomInfo")
    public boolean setPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode,
                                      @RequestParam(name = "field") String field) {
        System.out.println("SetPresentRoomInfo in room " + roomCode + " receives " + field);
        return runner.setPresentRoomInfo(roomCode, field);
    }

    @PostMapping("revealAllPresentRoomInfo")
    public boolean revealAllPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode) {
        System.out.println("revealAllPresentRoomInfo in room " + roomCode);
        return runner.revealAllFields(roomCode);
    }


    @PostMapping("/backToPresentRoom")
    public String backToPresentRoom(@RequestParam(name = "roomCode") String roomCode) {
        RoomStatus currentStat = runner.getStatus(roomCode);
        System.out.println("Back To Presenting Room, curStat " + currentStat);
        if (runner.changeRoomStatus(roomCode, RoomStatus.PRESENTING)) {
            // Reset target
            runner.setTargetInRoom(roomCode, new Target("", ""));
            if (currentStat == RoomStatus.WORDLING) {
                wordleService.returnToPresentingRoom(roomCode);
                wordleService.resetSession(roomCode);
                System.out.println("Reseting Wordle");
            } else if (currentStat == RoomStatus.HANGMAN) {
                hangmanService.returnToPresentingRoom(roomCode);
                hangmanService.resetSession(roomCode);
                System.out.println("Reseting Hangman");
            } else if (currentStat == RoomStatus.PICTURING || currentStat == RoomStatus.SHAREBOARD) {
                drawingService.returnToPresentingRoom(roomCode);
                System.out.println("Reseting Pictionary/Shareboard");
            }

            // Reset Timer when return to present room
            TimerMessage timerMessage = new TimerMessage(roomCode, RoomStatus.PRESENTING, 0);
            timerService.startTimer(timerMessage);

            return "Success";
        }
        return "Fail";
    }

}
