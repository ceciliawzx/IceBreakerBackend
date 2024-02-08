package com.icebreaker.services;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.websocket.TimerMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TimerService {

    private static final long INITIAL_DELAY = 0; // Start immediately
    private static final long PERIOD = 1; // Update every second

    private final SimpMessagingTemplate messagingTemplate;
    private ScheduledFuture<?> future;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private int countdown;
    private final DrawingService drawingService;
    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final ServerRunner runner = ServerRunner.getInstance();

    public TimerService(SimpMessagingTemplate messagingTemplate, DrawingService drawingService, WordleService wordleService, HangmanService hangmanService) {
        this.messagingTemplate = messagingTemplate;
        this.drawingService = drawingService;
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
    }

    public void startTimer(TimerMessage timerMessage) {
        countdown = timerMessage.getSeconds();
        String roomCode = timerMessage.getRoomCode();
        RoomStatus roomStatus = timerMessage.getRoomStatus();
        if (future != null) {
            future.cancel(false);
        }
        future = scheduler.scheduleAtFixedRate(() -> {
            if (countdown > 0) {
                countdown--;
                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", countdown);
            } else {
                stopTimer(timerMessage);
            }
        }, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
    }

    public void modifyTimer(TimerMessage timerMessage) {
        String roomCode = timerMessage.getRoomCode();
        int seconds = timerMessage.getSeconds();
        countdown += seconds;
        // Broadcast the updated countdown immediately
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", countdown);
    }

    // Stop the timer and notify frontend to navigate back to present room (when payload == 0)
    public void stopTimer(TimerMessage timerMessage) {
        String roomCode = timerMessage.getRoomCode();
        RoomStatus currentStat = runner.getStatus(roomCode);
        if (future != null) {
            future.cancel(false);
            // Broadcast navigation message or any other final action
            countdown = 0;
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", countdown);
            // Send showModal message
            if (currentStat == RoomStatus.PICTURING) {
                drawingService.showModal(roomCode);
            } else if (currentStat == RoomStatus.WORDLING) {
                wordleService.showModal(roomCode);
            } else if (currentStat == RoomStatus.HANGMAN) {
                hangmanService.showModal(roomCode);
            } else {
                System.out.println("Uncaught case in stopTimer");
            }
        }
    }
}
