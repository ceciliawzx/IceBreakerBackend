package com.icebreaker.services;

import com.icebreaker.dto.room.Room;
import com.icebreaker.enums.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
import com.icebreaker.dto.websocket.TimerMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class TimerService {

    private static final long INITIAL_DELAY = 0; // Start immediately
    private static final long PERIOD = 1; // Update every second

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> countdownMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final DrawingService drawingService;
    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final GeoguesserService geoguesserService;
    private final ChatService chatService;
    private final ServerRunner runner = ServerRunner.getInstance();

    public TimerService(SimpMessagingTemplate messagingTemplate, DrawingService drawingService, WordleService wordleService, HangmanService hangmanService, GeoguesserService geoguesserService, ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.drawingService = drawingService;
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
        this.geoguesserService = geoguesserService;
        this.chatService = chatService;
    }

    public void startTimer(TimerMessage timerMessage) {
        String roomCode = timerMessage.getRoomCode();
        countdownMap.put(roomCode, timerMessage.getSeconds());
        runner.getRoom(roomCode).setShowTimerModal(false);
        ScheduledFuture<?> currentFuture = futureMap.get(roomCode);
        if (currentFuture != null) {
            currentFuture.cancel(false);
        }
        currentFuture = scheduler.scheduleAtFixedRate(() -> {
            Integer countdown = countdownMap.getOrDefault(roomCode, 0);
            if (countdown > 0) {
                countdown--;
                countdownMap.put(roomCode, countdown);
                timerMessage.setSeconds(countdown);
                timerMessage.setStarted(true);
                // Broadcast this message to games to ban input before timer started
                hangmanService.broadCastTimerStarted(roomCode);
                wordleService.broadCastTimerStarted(roomCode);
                chatService.broadCastTimerStarted(roomCode);
                // Broadcast this message to services and update the timer
                messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", timerMessage);
            } else {
                stopTimer(timerMessage);
            }
        }, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);
        futureMap.put(roomCode, currentFuture);
    }

    public void modifyTimer(TimerMessage timerMessage) {
        String roomCode = timerMessage.getRoomCode();
        int seconds = timerMessage.getSeconds();
        Integer countdown = countdownMap.getOrDefault(roomCode, 0) + seconds;
        countdownMap.put(roomCode, countdown);
        // Broadcast the updated countdown immediately
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", countdown);
    }

    // Stop the timer and notify frontend to navigate back to present room (when payload == 0)
    public void stopTimer(TimerMessage timerMessage) {
        String roomCode = timerMessage.getRoomCode();
        RoomStatus currentStat = runner.getStatus(roomCode);
        ScheduledFuture<?> future = futureMap.get(roomCode);
        if (future != null) {
            future.cancel(false);
            futureMap.remove(roomCode);
            countdownMap.put(roomCode, 0);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/timer", 0);
            // Send showModal message
            switch (currentStat) {
                case WORDLING -> wordleService.showModal(roomCode);
                case HANGMAN -> hangmanService.showModal(roomCode);
                case PICTURING, SHAREBOARD -> drawingService.showModal(roomCode);
                case GEO_GUESSING -> geoguesserService.showModal(roomCode);
                case PRESENTING -> System.out.println("curState = PRESENTING in stopTimer");
                default -> System.out.println("Uncaught case in stopTimer: " + currentStat);
            }
        }
    }

    public void resetTimer(String roomCode) {
        ScheduledFuture<?> future = futureMap.get(roomCode);
        if (future != null) {
            future.cancel(false);
            futureMap.remove(roomCode);
        }
        countdownMap.remove(roomCode);
    }

    public void resetShowTimerModal(String roomCode) {
        Room room = runner.getRoom(roomCode);
        room.setShowTimerModal(true);
    }

    public String getShowTimerModal(String roomCode) {
        Room room = runner.getRoom(roomCode);
        if (room == null) {
            return JsonUtils.returnRoomNotFoundJsonError();
        }
        boolean showTimerModal = room.isShowTimerModal();
        return JsonUtils.returnJson(Map.of("showTimerModal", showTimerModal), JsonUtils.unknownError);
    }
}
