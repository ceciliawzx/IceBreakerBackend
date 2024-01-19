package com.icebreaker.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RoomCodeGenerator {
    private static final int MAX_CODE_VALUE = 10000;

    private Set<String> existingCodes;
    private Random random;

    public RoomCodeGenerator() {
        existingCodes = new HashSet<>();
        random = new Random();
    }

    private String generateRandomCode() {
        int code = random.nextInt(MAX_CODE_VALUE);
        return String.format("%04d", code);
    }

    private boolean isCodeExists(String code) {
        return existingCodes.contains(code);
    }

    public String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (isCodeExists(code));

        existingCodes.add(code);
        return code;
    }
}
