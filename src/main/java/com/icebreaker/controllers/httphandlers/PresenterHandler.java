package com.icebreaker.controllers.httphandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icebreaker.person.Person;
import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PresenterHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/changePresenter")
    public boolean changePresenter(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "userID") String userID) {
        System.out.println("Change Presenter in room: " + roomCode + " to User: " + userID);
        return runner.changePresenter(roomCode, userID);
    }

    @GetMapping("/notPresentedPeople")
    public String getNotPresentedPeople(@RequestParam(name = "roomCode") String roomCode) {
        System.out.println("Get not presented people in room: " + roomCode);
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
}
