package com.icebreaker.controllers;

import com.icebreaker.person.Admin;
import com.icebreaker.person.Person;
import com.icebreaker.room.GameType;
import com.icebreaker.room.Room;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.ChatService;
import com.icebreaker.services.WordleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.icebreaker.utils.HashUserId.hashUserId;

@RestController
public class HttpRequestsHandler {

    private final ChatService chatService;
    private final WordleService wordleService;

    @Autowired
    public HttpRequestsHandler(ChatService chatService, WordleService wordleService) {
        this.chatService = chatService;
        this.wordleService = wordleService;

    }
    private final AtomicInteger roomNumber = new AtomicInteger(0);
    private final AtomicInteger userID = new AtomicInteger(0);

    @GetMapping("/myEndpoint")
    public String handleRequest(@RequestParam(name = "message", required = false) String message) {
        System.out.println("My endPoint " + message);
        return "Received message: " + message;
    }

    @PostMapping("/createRoom")
    public String handleRoomCreation(@RequestParam(name = "name", required = true) String name)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        int newRoomNumber = roomNumber.getAndIncrement();

        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);
        ServerRunner runner = ServerRunner.getInstance();
        String roomCode = runner.getRoomCodeGenerator().generateUniqueCode();
        Admin admin = new Admin(name, roomCode, usb.toString());
        Room newRoom = new Room(newRoomNumber, roomCode, admin, chatService);

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(Map.of("userID", usb.toString(), "roomCode", roomCode));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        System.out.printf("Create Room: %s, %s, %s%n", name, usb.toString(), roomCode);

        return runner.addRoom(newRoom, roomCode) ? json : "Room Creation Failed";
    }


    @PostMapping("/joinRoom")
    public String handleJoinRoom(@RequestParam(name = "roomCode", required = true) String code,
                                 @RequestParam(name = "name", required = true) String name)
            throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-256");

        int newUserID = userID.getAndIncrement();
        StringBuilder usb = hashUserId(name, md, newUserID);

        ServerRunner runner = ServerRunner.getInstance();

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        try {
            json = objectMapper.writeValueAsString(Map.of("userID", usb.toString()));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        System.out.printf("Join Room: %s, %s, %s%n", name, usb.toString(), code);

        return runner.joinRoom(code, name, usb.toString()) ?
                json :
                "Join Room Failed";
    }

    @DeleteMapping("/destroyRoom")
    public boolean handleDestroyRoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        System.out.printf("Destroy Room: %s%n", roomCode);
        return runner.destroyRoom(roomCode);
    }

    @GetMapping("/isAdmin")
    public boolean isAdmin(@RequestParam("userID") String userID,
                                   @RequestParam("roomCode") String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        System.out.printf("Check Admin: %s, %s%n", userID, roomCode);
        try {
            return runner.isAdmin(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/isPresenter")
    public boolean isPresenter(@RequestParam("userID") String userID,
                                   @RequestParam("roomCode") String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        System.out.printf("Check Presenter: %s, %s%n", userID, roomCode);

        try {
            return runner.isPresenter(userID, roomCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping(path = "/updatePerson", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String updatePerson(@RequestBody Person person) {
        ServerRunner runner = ServerRunner.getInstance();
        System.out.printf("Update Person: %s, %s%n", person.getUserID(), person.getRoomCode());
        if (runner.roomUpdateUser(person)) {
            return "Success";
        } else {
            return "Fail";
        }
    }

    @DeleteMapping("/kickPerson")
    public boolean kickPerson(@RequestParam(name = "userID", required = true) String userID,
                                             @RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        System.out.printf("Kick Person: %s, %s%n", userID, roomCode);
        return runner.kickPerson(roomCode, userID);
    }

    @GetMapping("/getPlayers")
    public String getPlayersInARoom(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.printf("Get Players in room: %s%n", roomCode);
        if (runner.containsRoom(roomCode)) {
            Person admin = runner.getAdminInRoom(roomCode);
            Person presenter = runner.getPresenterInRoom(roomCode);
            RoomStatus status = runner.getStatus(roomCode);
            List<Person> otherPlayers = runner.getOtherPlayersInRoom(roomCode);

            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("admin", admin, "otherPlayers", otherPlayers, "presenter", presenter, "roomStatus", status));
            } catch (Exception e) {
                // Handle exception if JSON serialization fails
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
            }

            return json;
        }

        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", "Room not found"));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        return jsonError;
    }
    // Im: userID    Out: User class

    @GetMapping("/getPlayer")
    public String getPlayerInARoom(@RequestParam(name = "roomCode", required = true) String roomCode,
                                   @RequestParam(name = "userID", required = true) String userID) {
        ServerRunner runner = ServerRunner.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        if (!runner.containsRoom(roomCode)) {
            String jsonRoomError;
            try {
                jsonRoomError = objectMapper.writeValueAsString(Map.of("error", "Room Not Found"));
            } catch (Exception e) {
                e.printStackTrace();
                jsonRoomError = "{\"error\": \"Serialization error\"}";
            }
            return jsonRoomError;
        }
        Person person = runner.getOnePlayerInfo(roomCode, userID);
        System.out.printf("Get Player: %s, %s%n", userID, roomCode);
        if (person != null) {
            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("userInfo", person));
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }

            return json;
        }

        String jsonPersonError;
        try {
            jsonPersonError = objectMapper.writeValueAsString(Map.of("error", "Person Not Found"));
        } catch (Exception e) {
            e.printStackTrace();
            jsonPersonError = "{\"error\": \"Serialization error\"}";
        }
        return jsonPersonError;
    }

    @PostMapping("/startInput")
    public String startInput(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        System.out.printf("Start Room: %s%n", roomCode);
        if (runner.serverStartRoom(roomCode)) {
            return "Success";
        }
        return "Fail";
    }

    @GetMapping("/infoComplete")
    public boolean checkPlayerInfoComplete(@RequestParam(name = "roomCode", required = true) String roomCode,
                                           @RequestParam(name = "userID", required = true) String userID) {
        ServerRunner runner = ServerRunner.getInstance();
        System.out.printf("Info Complete: %s, %s%n", userID, roomCode);
        return runner.checkPlayerInfoComplete(roomCode, userID);
    }

    @PostMapping("/startDrawAndGuess")
    public String startDrawAndGuess(@RequestParam(name = "roomCode", required = true) String roomCode,
                                    @RequestParam(name = "target", required = true) String target) {
        ServerRunner runner = ServerRunner.getInstance();
        if (runner.changeRoomStatus(roomCode, RoomStatus.PICTURING)) {
            runner.setTargetInRoom(roomCode, target);
            return "Success";
        }
        return "Fail";
    }

    @PostMapping("/changePresenter")
    public boolean changePresenter(@RequestParam(name = "roomCode", required = true) String roomCode,
                                   @RequestParam(name = "userID", required = true) String userID) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.changePresenter(roomCode, userID);
    }

    @GetMapping("/notPresentedPeople")
    public String getNotPresentedPeople(@RequestParam(name = "roomCode", required = true) String roomCode) {
        ServerRunner runner = ServerRunner.getInstance();
        List<Person> notPresentedPeople = runner.getNotPresentedPeople(roomCode);
        ObjectMapper objectMapper = new ObjectMapper();

        if (notPresentedPeople != null && !notPresentedPeople.isEmpty()) {
            String json;

            try {
                json = objectMapper.writeValueAsString(Map.of("notPresentedPeople", notPresentedPeople));
            } catch (Exception e) {
                e.printStackTrace();
                json = "{\"error\": \"Serialization error\"}";
            }

            return json;
        }

        String jsonError;
        try {
            jsonError = objectMapper.writeValueAsString(Map.of("error", "Room not found"));
        } catch (Exception e) {
            // Handle exception if JSON serialization fails
            e.printStackTrace();
            jsonError = "{\"error\": \"Serialization error\"}"; // A fallback JSON response in case of an error
        }

        return jsonError;
    }

    @GetMapping("/availableGames")
    public List<GameType> availableGames(@RequestParam(name = "roomCode", required = true) String roomCode,
                                         @RequestParam(name = "userID", required = true) String userID,
                                         @RequestParam(name = "fieldName", required = true) String fieldName) {
        ServerRunner runner = ServerRunner.getInstance();
        return runner.availableGames(roomCode, userID, fieldName);
    }

    @PostMapping("/startWordle")
    public boolean startDrawAndGuess(@RequestParam(name = "roomCode", required = true) String roomCode,
                                     @RequestParam(name = "userID", required = true) String userID,
                                     @RequestParam(name = "field", required = true) String field) {
        System.out.println("Start Wordle: " + roomCode + " " + userID + " " + field);
        ServerRunner runner = ServerRunner.getInstance();
        if (runner.changeRoomStatus(roomCode, RoomStatus.WORDLING)) {
            String word = runner.getFieldValue(roomCode, userID, field);
            System.out.println("The word is: " + word);
            return wordleService.setAnswers(roomCode, word);
        }
        return false;
    }

    @GetMapping("/getWordleInfo")
    public int getWordleInfo(@RequestParam(name = "roomCode", required = true) String roomCode) {
        if (wordleService.roomExist(roomCode)) {
            System.out.println("The word is: " + wordleService.getAnswer(roomCode));
            System.out.println("With length: " + wordleService.getAnswer(roomCode).length());
            return wordleService.getAnswer(roomCode).length();
        }
        return -1;
    }
}
