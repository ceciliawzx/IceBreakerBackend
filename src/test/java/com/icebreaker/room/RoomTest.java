package com.icebreaker.room;

import com.icebreaker.person.Admin;
import com.icebreaker.person.Person;
import com.icebreaker.person.User;
import com.icebreaker.room.Room;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    private Room room;
    private Admin admin;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        admin = new Admin("Admin", "1111", "admin1");
        room = new Room(1, "ABC123", admin);
        user1 = new User("Alice", "1111", "user1", "test", "test", "test", "test", "test", "test", "test", "test", true);
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
        assertEquals(3, players.size());

        // Check if the user with ID "user2" is updated to "Charlie"
        assertEquals("Charlie", players.get(2).getDisplayName());

        room.updateUser(updateAdmin);

        assertEquals("David", players.get(0).getDisplayName());
    }

    @Test
    void testGetAvailableGamesForCountry() {
        String userID = "user1";
        String fieldName = "country";

        List<GameType> result = room.getAvailableGames(userID, fieldName);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(GameType.GEOGUESSER));
        assertTrue(result.contains(GameType.WORDLE));
        assertTrue(result.contains(GameType.PICTIONARY));
        assertTrue(result.contains(GameType.SHAREBOARD));
        assertTrue(result.contains(GameType.HANGMAN));
    }

    @Test
    void testGetAvailableGamesForFavs() {
        String userID = "user1";
        String fieldName = "favFood";

        List<GameType> result = room.getAvailableGames(userID, fieldName);

        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.contains(GameType.WORDLE));
        assertTrue(result.contains(GameType.PICTIONARY));
        assertTrue(result.contains(GameType.SHAREBOARD));
        assertTrue(result.contains(GameType.HANGMAN));
    }

}
