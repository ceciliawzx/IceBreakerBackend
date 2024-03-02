package com.icebreaker.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasteImgMessage {

    @Data
    public static class PasteImgData {
        private String imgUrl;
    }

    private String roomCode;
    private PasteImgData pasteImgData;
    private LocalDateTime timestamp;
    private String paster;

    @Override
    public String toString() {
        return paster + " has pasted an img: " + pasteImgData + " in room: " + roomCode + " at time: " + timestamp.toString();
    }
}
