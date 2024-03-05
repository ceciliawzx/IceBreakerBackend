package com.icebreaker.controllers;

import com.icebreaker.services.DrawingService;
import com.icebreaker.websocket.DrawingMessage;
import com.icebreaker.websocket.PasteImgMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class DrawingController {

    private final DrawingService drawingService;

    @Autowired
    public DrawingController(DrawingService drawingService) {
        this.drawingService = drawingService;
    }

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

}
