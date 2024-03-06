package com.icebreaker.serverrunner;

import com.icebreaker.dto.room.Room;
import com.icebreaker.utils.RoomCodeGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ServerRunnerTest {

    private ServerRunner serverRunner;
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        serverRunner = ServerRunner.getInstance();
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    public void serverRunnerOnlyCreatesOneInstance() {
        ServerRunner instance2 = ServerRunner.getInstance();
        assertSame(serverRunner, instance2);
    }

    @Test
    public void serverRunnerCanAddAndDestroyRoom() {
        RoomCodeGenerator roomCodeGenerator = new RoomCodeGenerator();
        String roomCode = roomCodeGenerator.generateUniqueCode();
        Room room = new Room(123, roomCode, null);

        // Mock behavior for HttpServletRequest
        when(request.getAttribute("someAttribute")).thenReturn("someValue");

        assertTrue(serverRunner.addRoom(room, roomCode));
        assertTrue(serverRunner.containsRoom(room));
        assertTrue(serverRunner.containsRoom(123));
        assertTrue(serverRunner.destroyRoom(123));
        assertFalse(serverRunner.containsRoom(room));
        assertFalse(serverRunner.containsRoom(123));
    }

}
