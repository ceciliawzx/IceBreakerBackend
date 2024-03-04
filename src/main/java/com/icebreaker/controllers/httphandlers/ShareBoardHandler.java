package com.icebreaker.controllers.httphandlers;

import com.icebreaker.room.RoomStatus;
import com.icebreaker.room.Target;
import com.icebreaker.serverrunner.ServerRunner;
import com.icebreaker.services.WaitRoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShareBoardHandler {
    private final ServerRunner runner = ServerRunner.getInstance();
    private final WaitRoomService waitRoomService;

    public ShareBoardHandler(WaitRoomService waitRoomService) {
        this.waitRoomService = waitRoomService;
    }

    @PostMapping("/startShareBoard")
    public boolean startShareBoard(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "fieldName") String fieldName) {
        if (runner.changeRoomStatus(roomCode, RoomStatus.SHAREBOARD)) {
            runner.setTargetInRoom(roomCode, new Target(fieldName, ""));
            waitRoomService.broadcastMessage(roomCode);
            return true;
        }
        return false;
    }
}
