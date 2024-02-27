package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WaitRoomHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;
    public WaitRoomHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }


    @PostMapping("/backToWaitRoom")
    public String backToWaitRoom(@RequestParam(name = "roomCode") String roomCode) {
        System.out.println("Back to wait room: " + roomCode);
        if (runner.changeRoomStatus(roomCode, RoomStatus.WAITING)) {
            // Reset Target
            runner.setTargetInRoom(roomCode, new Target("", ""));
            // add presented person to presentedList
            runner.addToPresentedList(roomCode);
            waitRoomService.broadcastMessage(roomCode);
            // Reset presentRoomInfo when back to WaitRoom
            return runner.resetPresentRoomInfo(roomCode) ? "Success" : "Fail";
        }
        return "Fail";
    }

    @PostMapping("/forceBackToAllPresentedRoom")
    public String forceBackToAllPresentedRoom(@RequestParam(name = "roomCode") String roomCode) {
        if (runner.forceBackToAllPresentedRoom(roomCode)) {
            waitRoomService.broadcastMessage(roomCode);
            return "Success";
        }
        return "Fail";
    }
}
