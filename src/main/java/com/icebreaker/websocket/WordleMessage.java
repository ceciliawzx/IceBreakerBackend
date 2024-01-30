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
    public static class Letters {
        private Character letter;
        private WordleStateCode state;
        public Letters(char l, WordleStateCode s) {
            letter = l;
            state = s;
        }

        @Override
        public String toString() {
            return "{ Letter: " + letter.toString() + " State: " + state.toString() + " }";
        }
    }


    private Boolean isCheck;
    private List<Letters> letters;
    private String roomCode;

//    public boolean getIsCheck() {
//        return isCheck;
//    }
//    public void setIsCheck(boolean b) {
//        this.isCheck = b;
//    }
    @Override
    public String toString() {
        return "Is Check: " + isCheck + " Letters: " + letters.toString() + " RoomCode: " + roomCode;
    }
}

