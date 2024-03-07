package com.icebreaker.controllers;

import com.icebreaker.dto.person.Person;
import com.icebreaker.services.PersonService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/isAdmin")
    public boolean isAdmin(@RequestParam("userID") String userID, @RequestParam("roomCode") String roomCode) {
        return personService.isAdmin(userID, roomCode);
    }

    @GetMapping("/isPresenter")
    public boolean isPresenter(@RequestParam("userID") String userID, @RequestParam("roomCode") String roomCode) {
        return personService.isPresenter(userID, roomCode);
    }

    @PostMapping(path = "/updatePerson", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String updatePerson(@RequestBody Person person) {
        return personService.updatePerson(person);
    }

    @DeleteMapping("/kickPerson")
    public boolean kickPerson(@RequestParam(name = "userID") String userID, @RequestParam(name = "roomCode") String roomCode) {
        return personService.kickPerson(roomCode, userID);
    }

    @GetMapping("/isNotified")
    public boolean isNotified(@RequestParam(name = "roomCode") String roomCode, @RequestParam(name = "userID") String userID) {
        return personService.isNotified(roomCode, userID);
    }


}
