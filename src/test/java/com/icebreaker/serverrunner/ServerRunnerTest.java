package com.icebreaker.serverrunner;

import static org.junit.Assert.*;

import com.icebreaker.room.Room;
import jakarta.servlet.http.HttpServletRequest;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.jmock.Expectations;
import org.jmock.Mockery;


public class ServerRunnerTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    private final ServerRunner serverRunner = ServerRunner.getInstance();
    private final HttpServletRequest request = context.mock(HttpServletRequest.class);

    @Test
    public void serverRunnerOnlyCreatesOneInstance() {
        ServerRunner instance2 = ServerRunner.getInstance();
        assertSame(serverRunner, instance2);
    }

    @Test
    public void serverRunnerCanAddAndDestroyRoom() {
        Room room = new Room(123, request);
        assertTrue(serverRunner.addRoom(room));
        assertTrue(serverRunner.containsRoom(room));
        assertTrue(serverRunner.containsRoom(123));
        assertTrue(serverRunner.destroyRoom(123));
        assertFalse(serverRunner.containsRoom(room));
        assertFalse(serverRunner.containsRoom(123));
    }


}
