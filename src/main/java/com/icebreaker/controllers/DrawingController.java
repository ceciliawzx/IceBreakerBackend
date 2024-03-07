package com.icebreaker.controllers;

import com.icebreaker.dto.websocket.DrawingMessage;
import com.icebreaker.dto.websocket.PasteImgMessage;
import com.icebreaker.services.DrawingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class DrawingController {

    private final DrawingService drawingService;

    @Autowired
    public DrawingController(DrawingService drawingService) {
        this.drawingService = drawingService;
    }

    /* WebSocket */
    @MessageMapping("/room/{roomCode}/sendDrawing")
    public void handleDrawing(@Payload DrawingMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        drawingService.broadcastDrawing(roomCode, message);
    }

    @MessageMapping("/room/{roomCode}/sendPasteImg")
    public void handlePasteImg(@Payload PasteImgMessage message) {
        String roomCode = String.valueOf(message.getRoomCode());
        drawingService.broadcastPasteImg(roomCode, message);
    }

    /* HTTP Handler */
    @PostMapping("/startDrawAndGuess")
    public boolean startDrawAndGuess(@RequestParam(name = "roomCode") String roomCode,
                                     @RequestParam(name = "fieldName") String fieldName,
                                     @RequestParam(name = "targetWord") String targetWord) {
        return drawingService.startDrawAndGuess(roomCode, fieldName, targetWord);
    }

    @PostMapping("/startShareBoard")
    public boolean startShareBoard(@RequestParam(name = "roomCode") String roomCode,
                                   @RequestParam(name = "fieldName") String fieldName) {
        return drawingService.startShareBoard(roomCode, fieldName);
    }

    @GetMapping("/getTarget")
    public String getTarget(@RequestParam(name = "roomCode") String roomCode) {
        return drawingService.getTarget(roomCode);
    }

}
