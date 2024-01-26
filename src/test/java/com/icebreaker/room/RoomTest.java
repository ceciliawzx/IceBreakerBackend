package com.icebreaker.room;

import com.icebreaker.person.Admin;
import com.icebreaker.person.Person;
import com.icebreaker.person.User;
import com.icebreaker.room.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RoomTest {

    private Room room;
    private Admin admin;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        admin = new Admin("Admin", "1111", "admin1");
        room = new Room(1, "ABC123", admin,  null);
        user1 = new User("Alice", "1111", "user1");
        user2 = new User("Bob", "1111", "user2");
        room.joinRoom(user1);
        room.joinRoom(user2);
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = new User("Charlie", "1111", "user2");
        Admin updateAdmin = new Admin("David", "1111", "admin1");
        room.updateUser(updatedUser);

        List<Person> players = room.getPlayers();
        Assertions.assertEquals(3, players.size());

        // Check if the user with ID "user2" is updated to "Charlie"
        Assertions.assertEquals("Charlie", players.get(2).getDisplayName());

        room.updateUser(updateAdmin);

        Assertions.assertEquals("David", players.get(0).getDisplayName());
    }
}
