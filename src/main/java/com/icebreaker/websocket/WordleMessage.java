package com.icebreaker.websocket;

import lombok.*;

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
    public static class WordleLetter {
        private String letter;
        private WordleStateCode state;
        public WordleLetter(String l, WordleStateCode s) {
            letter = l;
            state = s;
        }

        @Override
        public String toString() {
            if (letter != null) {
                return "{ Letter: " + letter.toString() + " State: " + state.toString() + " }";
            } else {
                return "{null}";
            }
        }
    }
    private Integer currentAttempt;
    private Integer totalAttempt;
    private Boolean isCheck;
    private List<List<WordleLetter>> letters;
    private String roomCode;
    private Boolean isCorrect;
    private List<WordleStateCode> allLetterStat;
    @Override
    public String toString() {
        return "Total Attempts: " + totalAttempt + " Current Attempt: " + currentAttempt + " Is Check: " + isCheck +
                " Letters: " + letters.toString() + " RoomCode: " + roomCode;
    }
}

