package com.icebreaker.controllers.httphandlers;

import com.icebreaker.person.Person;
import com.icebreaker.room.RoomStatus;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import com.icebreaker.utils.JsonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PresenterHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;
    public PresenterHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("/changePresenter")
    public boolean changePresenter(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "userID") String userID) {
        System.out.println("Change Presenter in room: " + roomCode + " to User: " + userID);
        boolean result = runner.changePresenter(roomCode, userID);
        waitRoomService.broadcastMessage(roomCode);
        return result;
    }

    @GetMapping("/notPresentedPeople")
    public String getNotPresentedPeople(@RequestParam(name = "roomCode") String roomCode) {
        List<Person> notPresentedPeople = runner.getNotPresentedPeople(roomCode);
        if (notPresentedPeople.isEmpty()) {
            runner.changeRoomStatus(roomCode, RoomStatus.ALL_PRESENTED);
            waitRoomService.broadcastMessage(roomCode);
        }

        if (notPresentedPeople != null) {
            return JsonUtils.returnJson(Map.of("notPresentedPeople", notPresentedPeople), JsonUtils.unknownError);
        }

        return JsonUtils.returnJsonError("Room not found");
    }
}
