package com.icebreaker.room;

import com.icebreaker.dto.person.Admin;
import com.icebreaker.dto.person.Person;
import com.icebreaker.dto.person.User;
import com.icebreaker.enums.GameType;
import com.icebreaker.dto.room.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    private Room room;
    private Admin admin;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void setUp() {
        admin = new Admin("Admin", "1111", "admin1");
        room = new Room(1, "ABC123", admin);
        user1 = new User("Alice", "1111", "user1", "test", "test", "test", "test", "test", "test", "test", "test", true);
        user2 = new User("Bob", "1111", "user2");
        user3 = new User("Elaine", "1111", "user3", "test", "test", "test", "te st", "test", "test", "te@st", "test", true);
        room.joinRoom(user1);
        room.joinRoom(user2);
        room.joinRoom(user3);
    }


    @Test
    public void testUpdateUser() {
        User updatedUser = new User("Charlie", "1111", "user2");
        Admin updateAdmin = new Admin("David", "1111", "admin1");
        room.updateUser(updatedUser);

        List<Person> players = room.getPlayers();
        assertEquals(4, players.size());

        // Check if the user with ID "user2" is updated to "Charlie"
        assertEquals("Charlie", players.get(3).getDisplayName());

        room.updateUser(updateAdmin);

        assertEquals("David", players.get(0).getDisplayName());
    }

    @Test
    void testGetAvailableGamesForCountry() {
        String userID = "user1";
        String fieldName = "country";

        List<GameType> result = room.getAvailableGames(userID, fieldName);

        assertNotNull(result);
        assertEquals(6, result.size());
        assertTrue(result.contains(GameType.GEOGUESSER));
        assertTrue(result.contains(GameType.WORDLE));
        assertTrue(result.contains(GameType.PICTIONARY));
        assertTrue(result.contains(GameType.SHAREBOARD));
        assertTrue(result.contains(GameType.HANGMAN));
        assertTrue(result.contains(GameType.REVEAL));
    }

    @Test
    void testGetAvailableGamesForFav() {
        String userID = "user1";
        String fieldName = "favFood";

        List<GameType> result = room.getAvailableGames(userID, fieldName);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(GameType.PICTIONARY));
        assertTrue(result.contains(GameType.SHAREBOARD));
        assertTrue(result.contains(GameType.HANGMAN));
        assertTrue(result.contains(GameType.REVEAL));
        assertTrue(result.contains(GameType.WORDLE));
    }

    @Test
    void testGetAvailableGamesSpace() {
        String userID = "user3";
        String fieldName = "country";

        List<GameType> result = room.getAvailableGames(userID, fieldName);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(GameType.GEOGUESSER));
        assertTrue(result.contains(GameType.PICTIONARY));
        assertTrue(result.contains(GameType.SHAREBOARD));
        assertTrue(result.contains(GameType.HANGMAN));
        assertTrue(result.contains(GameType.REVEAL));
    }

    @Test
    void testGetAvailableGamesSpecialChars() {
        String userID = "user3";
        String fieldName = "favFood";

        List<GameType> result = room.getAvailableGames(userID, fieldName);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(GameType.PICTIONARY));
        assertTrue(result.contains(GameType.SHAREBOARD));
        assertTrue(result.contains(GameType.REVEAL));

    }

}
