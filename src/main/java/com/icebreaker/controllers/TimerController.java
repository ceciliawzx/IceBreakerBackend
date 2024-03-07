package com.icebreaker.controllers;

import com.icebreaker.dto.websocket.TimerMessage;
import com.icebreaker.services.TimerService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Controller
@RestController
public class TimerController {

    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    @GetMapping("/getShowTimerModal")
    public String getShowTimerModal(@RequestParam(name = "roomCode") String roomCode) {
        return timerService.getShowTimerModal(roomCode);
    }

    // Start the timer
    @MessageMapping("/room/{roomCode}/startTimer")
    public void startTimer(@Payload TimerMessage timerMessage) {
        timerService.startTimer(timerMessage);
    }

    // Add time to timer
    @MessageMapping("/room/{roomCode}/modifyTimer")
    public void modifyTimer(@Payload TimerMessage timerMessage) {
        timerService.modifyTimer(timerMessage);
    }

    // Stop the timer (skip the timer)
    @MessageMapping("/room/{roomCode}/stopTimer")
    public void stopTimer(@Payload TimerMessage timerMessage) {
        timerService.stopTimer(timerMessage);
    }

}
