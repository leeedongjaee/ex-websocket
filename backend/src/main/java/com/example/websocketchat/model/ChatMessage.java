package com.example.websocketchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    private MessageType type;
    private String sender;
    private String content;
    private String timestamp;
    
    public enum MessageType {
        CHAT,    // 일반 채팅 메시지
        JOIN,    // 입장 메시지
        LEAVE    // 퇴장 메시지
    }
}
