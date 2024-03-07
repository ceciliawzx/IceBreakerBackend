package com.icebreaker.dto.websocket;

import com.icebreaker.enums.WordleStateCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordleMessage {

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
                return "{ Letter: " + letter + " State: " + state.toString() + " }";
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

