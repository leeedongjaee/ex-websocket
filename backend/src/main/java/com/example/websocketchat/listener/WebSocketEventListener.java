package com.example.websocketchat.listener;

import com.example.websocketchat.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEventListener(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * WebSocket 연결 이벤트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("새로운 WebSocket 연결");
    }

    /**
     * WebSocket 연결 해제 이벤트
     * 사용자가 채팅방을 나가면 퇴장 메시지를 브로드캐스트
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        
        if (username != null) {
            log.info("사용자 퇴장: {}", username);
            
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            chatMessage.setContent(username + "님이 퇴장했습니다.");
            chatMessage.setTimestamp(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
}
