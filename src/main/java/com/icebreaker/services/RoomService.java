package com.icebreaker.services;

import com.icebreaker.dto.person.Admin;
import com.icebreaker.dto.person.Person;
import com.icebreaker.dto.room.PresentRoomInfo;
import com.icebreaker.dto.room.Room;
import com.icebreaker.dto.room.Target;
import com.icebreaker.enums.GameType;
import com.icebreaker.enums.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.icebreaker.utils.HashUserId.hashUserId;

@Service
public class RoomService {

    private final WordleService wordleService;
    private final HangmanService hangmanService;
    private final DrawingService drawingService;
    private final TimerService timerService;
    private final WaitRoomService waitRoomService;
    private final GeoguesserService geoguesserService;

    private final ServerRunner runner = ServerRunner.getInstance();
    private final AtomicInteger roomNumber = new AtomicInteger(0);
    private final AtomicInteger userID = new AtomicInteger(0);

    public RoomService(WordleService wordleService, HangmanService hangmanService, DrawingService drawingService, TimerService timerService, WaitRoomService waitRoomService, GeoguesserService geoguesserService) {
        this.wordleService = wordleService;
        this.hangmanService = hangmanService;
        this.drawingService = drawingService;
        this.timerService = timerService;
        this.waitRoomService = waitRoomService;
        this.geoguesserService = geoguesserService;
    }

    public String handleCreateRoom(String name) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        int newRoomNumber = roomNumber.getAndIncrement();
        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);
        String roomCode = runner.getRoomCodeGenerator().generateUniqueCode();
        Admin admin = new Admin(name, roomCode, usb.toString());
        Room newRoom = new Room(newRoomNumber, roomCode, admin);
        String result = runner.addRoom(newRoom, roomCode) ? JsonUtils.returnJson(
                Map.of("userID", usb.toString(), "roomCode", roomCode), "Room Creation Failed"
        ) : "Room Creation Failed";
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    public String handleJoinRoom(String code, String name) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);
        if (runner.joinRoom(code, name, usb.toString())) {
            waitRoomService.broadcastMessage(code);
            return JsonUtils.returnJson(
                    Map.of("userID", usb.toString()), "Join Room Failed");
        } else {
            return JsonUtils.roomNotFound;
        }
    }

    public boolean handleDestroyRoom(String roomCode) {
        boolean result = runner.destroyRoom(roomCode);
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    public List<GameType> availableGames(String roomCode, String userID, String fieldName) {
        return runner.availableGames(roomCode, userID, fieldName);
    }

    public String getPlayersInARoom(String roomCode) {
        if (runner.containsRoom(roomCode)) {
            Person admin = runner.getAdminInRoom(roomCode);
            Person presenter = runner.getPresenterInRoom(roomCode);
            RoomStatus status = runner.getStatus(roomCode);
            List<Person> otherPlayers = runner.getOtherPlayersInRoom(roomCode);
            return JsonUtils.returnJson(Map.of("admin", admin, "otherPlayers", otherPlayers, "presenter", presenter, "roomStatus", status), JsonUtils.unknownError);
        }
        return JsonUtils.returnRoomNotFoundJsonError();
    }

    public String getPlayer(String roomCode, String userID) {
        if (!runner.containsRoom(roomCode)) {
            return JsonUtils.returnRoomNotFoundJsonError();
        }
        Person person = runner.getOnePlayerInfo(roomCode, userID);
        if (person != null) {
            return JsonUtils.returnJson(Map.of("userInfo", person), JsonUtils.personNotFound);
        }
        return JsonUtils.returnPersonNotFoundJsonError();
    }

    public String getPresenterInARoom(String roomCode) {
        if (!runner.containsRoom(roomCode)) {
            return JsonUtils.returnRoomNotFoundJsonError();
        }
        Person presenter = runner.getPresenterInRoom(roomCode);
        if (presenter != null) {
            return JsonUtils.returnJson(Map.of("presenter", presenter), JsonUtils.personNotFound);
        }
        return JsonUtils.returnPersonNotFoundJsonError();
    }

    public boolean pushNotification(String roomCode, String userID) {
        boolean result = runner.notifyPeople(roomCode, userID);
        if (result) {
            waitRoomService.broadcastMessage(roomCode);
        }
        return result;
    }

    public boolean isNotified(String roomCode, String userID) {
        return runner.isNotified(roomCode, userID);
    }

    public boolean startPresenting(String roomCode) {
        boolean result = runner.startPresenting(roomCode);
        if (result) {
            waitRoomService.broadcastMessage(roomCode);
        }
        return result;
    }

    public boolean checkPlayerInfoComplete(String roomCode, String userID) {
        return runner.checkPlayerInfoComplete(roomCode, userID);
    }

    public boolean changePresenter(String roomCode, String userID) {
        boolean result = runner.changePresenter(roomCode, userID);
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    public String getNotPresentedPeople(String roomCode) {
        List<Person> notPresentedPeople = runner.getNotPresentedPeople(roomCode);
        if (notPresentedPeople == null) {
            return JsonUtils.returnJsonError("Room not found");
        }
        if (notPresentedPeople.isEmpty()) {
            runner.changeRoomStatus(roomCode, RoomStatus.ALL_PRESENTED);
            waitRoomService.broadcastMessage(roomCode);
        }
        return JsonUtils.returnJson(Map.of("notPresentedPeople", notPresentedPeople), JsonUtils.roomNotFound);
    }


    public String getPresentRoomInfo(String roomCode) {
        PresentRoomInfo presentRoomInfo = runner.getPresentRoomInfo(roomCode);
        return JsonUtils.returnJson(Map.of("presentRoomInfo", presentRoomInfo), "Room not found");
    }

    public boolean setPresentRoomInfo(String roomCode, String field) {
        boolean result = runner.setPresentRoomInfo(roomCode, field);
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    public boolean revealAllPresentRoomInfo(String roomCode) {
        boolean result = runner.revealAllFields(roomCode);
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    public boolean backToPresentRoom(String roomCode) {
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
