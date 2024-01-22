package com.icebreaker.controllers;

import com.icebreaker.services.DrawingService;
import com.icebreaker.websocket.DrawingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
//        System.out.println("Receiving drawing message " + message + "from room " + roomCode);
        String roomCode = String.valueOf(message.getRoomCode());
        drawingService.broadcastDrawing(roomCode, message);
    }

}
