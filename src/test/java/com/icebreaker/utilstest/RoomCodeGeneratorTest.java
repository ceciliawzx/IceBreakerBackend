package com.icebreaker.utilstest;


import com.icebreaker.utils.RoomCodeGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoomCodeGeneratorTest {

    @Test
    public void testGenerateUniqueCode() {
        RoomCodeGenerator codeGenerator = new RoomCodeGenerator();
        Set<String> generatedCodes = new HashSet<>();

        for (int i = 0; i < 10000; i++) {
            String roomCode = codeGenerator.generateUniqueCode();
            assertFalse(generatedCodes.contains(roomCode), "Generated duplicate code: " + roomCode);
            assertTrue(isValidRoomCode(roomCode), "Generated invalid code: " + roomCode);
            generatedCodes.add(roomCode);
        }
    }

    private boolean isValidRoomCode(String code) {
        // Check if the code is a 4-digit number
        return code.matches("\\d{4}");
    }
}
