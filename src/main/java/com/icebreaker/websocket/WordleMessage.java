package com.icebreaker.websocket;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordleMessage {

    // API Tell which game can play
    // userID, roomCode, Field

    // API Wordle start, change room status to wordle, save correct answer in wordle service
    // roomCode, userID, Field
    // json(list_of_player in tern, num of char in answer)

    // At each letter change use websocket:
    // List<Letter> letters; // All letters are unchecked

    // Websocket Return
    // List<Letter> letters
    // Class Letter(char char, int state)


    // Tried word before and letter status
    // Who's tern
    // Correct?

    @Data
    @Getter
    public static class Letters {
        private final char letter;
        private final int state;
        public Letters(char letter, int state) {
            this.letter = letter;
            this.state = state;
        }
    }

    private boolean isCheck;
    private List<Letters> letters;
    private int roomCode;

    @Override
    public String toString() {
        return "";
    }
}

