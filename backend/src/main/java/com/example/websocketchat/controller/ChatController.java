package com.example.websocketchat.controller;

import com.example.websocketchat.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    /**
     * 채팅 메시지 전송
     * 클라이언트가 /app/chat.sendMessage로 메시지를 보내면
     * /topic/public 을 구독하고 있는 모든 클라이언트에게 브로드캐스트
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return chatMessage;
    }

    /**
     * 사용자 입장
     * 클라이언트가 /app/chat.addUser로 메시지를 보내면
     * /topic/public 을 구독하고 있는 모든 클라이언트에게 브로드캐스트
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                                SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + "님이 입장했습니다.");
        chatMessage.setTimestamp(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        
        return chatMessage;
    }
}
