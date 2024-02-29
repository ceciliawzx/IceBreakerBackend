package com.icebreaker.controllers;

import com.icebreaker.services.DrawingService;
import com.icebreaker.services.HangmanService;
import com.icebreaker.services.TimerService;
import com.icebreaker.services.WordleService;
import com.icebreaker.websocket.TimerMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class TimerController {

    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    // Endpoint to start the timer
    @MessageMapping("/room/{roomCode}/startTimer")
    public void startTimer(@Payload TimerMessage timerMessage) {
        System.out.println("Server receives timerMessage in startTimer: " + timerMessage);
        timerService.startTimer(timerMessage);
    }

    // Endpoint to add time to timer
    @MessageMapping("/room/{roomCode}/modifyTimer")
    public void modifyTimer(@Payload TimerMessage timerMessage) {
        System.out.println("Server receives timerMessage in modifyTimer: " + timerMessage);
        timerService.modifyTimer(timerMessage);
    }

    // Endpoint to stop the timer (skip the timer)
    @MessageMapping("/room/{roomCode}/stopTimer")
    public void stopTimer(@Payload TimerMessage timerMessage) {
        System.out.println("Server receives timerMessage in stopTimer: " + timerMessage);
        timerService.stopTimer(timerMessage);
    }

}
