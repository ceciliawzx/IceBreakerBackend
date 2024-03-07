package com.icebreaker.controllers;

import com.icebreaker.services.MockRoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockRoomController {

    private final MockRoomService mockRoomService;

    public MockRoomController(MockRoomService mockRoomService) {
        this.mockRoomService = mockRoomService;
    }

    @PostMapping("/restartMockRoom")
    public boolean restartMockRoom() {
        return mockRoomService.restartMockRoom();
    }
}
