package com.icebreaker.controllers.httphandlers;

import com.icebreaker.serverrunner.ServerRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockRoomHandler {
    private final ServerRunner runner = ServerRunner.getInstance();

    @PostMapping("/restartMockRoom")
    public boolean restartMockRoom() {
        System.out.println("Restart Mock Room");
        return runner.restartMockRoom();
    }
}
