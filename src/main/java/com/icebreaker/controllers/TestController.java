//package com.icebreaker.controllers;
//
//import com.icebreaker.websocket.ChatMessage;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class TestController {
//
//    private final ChatController chatController;
//
//    public TestController(ChatController chatController) {
//        this.chatController = chatController;
//    }
//
//    @GetMapping("/testBroadcast")
//    public String testBroadcast() {
//        ChatMessage testMessage = new ChatMessage();
//        testMessage.setContent("This is a test broadcast message");
//        chatController.broadcastToRoom(0, testMessage); // Room number is 0 for testing
//        return "Broadcast sent";
//    }
//}
