package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.PresentRoomInfo;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.*;
import com.icebreaker.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
public class PresentRoomController {
    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final DrawingService drawingService;
    private final TimerService timerService;
    private final WaitRoomService waitRoomService;
    private final GeoguesserService geoguesserService;
    private final ServerRunner runner = ServerRunner.getInstance();

    @Autowired
    public PresentRoomController(WordleService wordleService, HangmanService hangmanService, DrawingService drawingService, TimerService timerService, WaitRoomService waitRoomService, GeoguesserService geoguesserService) {
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
        this.drawingService = drawingService;
        this.timerService = timerService;
        this.waitRoomService = waitRoomService;
        this.geoguesserService = geoguesserService;
    }

    @GetMapping("/getPresentRoomInfo")
    public String getPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode) {
        PresentRoomInfo presentRoomInfo = runner.getPresentRoomInfo(roomCode);
        return JsonUtils.returnJson(Map.of("presentRoomInfo", presentRoomInfo), "Room not found");
    }

    @PostMapping("setPresentRoomInfo")
    public boolean setPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode,
                                      @RequestParam(name = "field") String field) {
        boolean result = runner.setPresentRoomInfo(roomCode, field);
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    @PostMapping("revealAllPresentRoomInfo")
    public boolean revealAllPresentRoomInfo(@RequestParam(name = "roomCode") String roomCode) {
        boolean result = runner.revealAllFields(roomCode);
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    @PostMapping("/backToPresentRoom")
    public boolean backToPresentRoom(@RequestParam(name = "roomCode") String roomCode) {
        RoomStatus currentStat = runner.getStatus(roomCode);
        if (runner.changeRoomStatus(roomCode, RoomStatus.PRESENTING)) {
            // Reset target
            runner.setTargetInRoom(roomCode, new Target("", ""));
            switch (currentStat) {
                case WORDLING -> {
                    wordleService.returnToPresentingRoom(roomCode);
                    wordleService.resetSession(roomCode);
                    waitRoomService.broadcastMessage(roomCode);
                }
                case HANGMAN -> {
                    hangmanService.returnToPresentingRoom(roomCode);
                    hangmanService.resetSession(roomCode);
                    waitRoomService.broadcastMessage(roomCode);
                }
                case PICTURING, SHAREBOARD -> {
                    drawingService.returnToPresentingRoom(roomCode);
                    waitRoomService.broadcastMessage(roomCode);
                }
                case GEO_GUESSING -> {
                    geoguesserService.returnToPresentingRoom(roomCode);
                    waitRoomService.broadcastMessage(roomCode);
                }
                default -> System.out.println("Uncaught case in backToPresentRoom");
            }
            // Reset Timer and showTimerModal when return to present room
            timerService.resetShowTimerModal(roomCode);
            timerService.resetTimer(roomCode);

            return true;
        }
        return false;
    }

}
