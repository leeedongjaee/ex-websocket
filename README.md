# 🚀 WebSocket 실시간 채팅 시스템

Spring Boot + React로 구현한 완전 기본 설정의 실시간 채팅 애플리케이션입니다.

## 📚 기술 스택

### Backend
- Spring Boot 3.2.0
- Spring WebSocket
- STOMP Protocol
- Lombok

### Frontend
- React 18.2
- Vite
- @stomp/stompjs
- SockJS Client

## 🛠️ 실행 방법

### 1. Backend 실행

```bash
cd backend

# Maven으로 실행
./mvnw spring-boot:run

# 또는 Gradle로 실행 (build.gradle 필요)
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 실행됩니다.

### 2. Frontend 실행

```bash
cd frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

프론트엔드가 `http://localhost:3000`에서 실행됩니다.

## 📁 프로젝트 구조

### Backend

```
backend/
├── src/main/java/com/example/websocketchat/
│   ├── WebSocketChatApplication.java          # 메인 애플리케이션
│   ├── config/
│   │   ├── WebSocketConfig.java               # WebSocket & STOMP 설정
│   │   └── WebConfig.java                     # CORS 설정
│   ├── controller/
│   │   └── ChatController.java                # 채팅 메시지 핸들러
│   ├── model/
│   │   └── ChatMessage.java                   # 채팅 메시지 DTO
│   └── listener/
│       └── WebSocketEventListener.java        # 입/퇴장 이벤트 리스너
└── src/main/resources/
    └── application.yml                        # 애플리케이션 설정
```

### Frontend

```
frontend/
├── src/
│   ├── App.jsx          # 메인 컴포넌트 (로그인 + 채팅)
│   ├── main.jsx         # React 엔트리 포인트
│   └── index.css        # 전역 스타일
├── index.html           # HTML 템플릿
├── vite.config.js       # Vite 설정
└── package.json         # 의존성 관리
```

## 🔧 주요 기능

### WebSocket & STOMP 통신

1. **연결 설정**
   - 엔드포인트: `/ws-chat`
   - SockJS 폴백 지원
   - STOMP over WebSocket

2. **메시지 타입**
   - `CHAT`: 일반 채팅 메시지
   - `JOIN`: 사용자 입장
   - `LEAVE`: 사용자 퇴장

3. **Destination 구조**
   - `/app/chat.sendMessage` → 메시지 전송
   - `/app/chat.addUser` → 사용자 추가
   - `/topic/public` → 모든 사용자 구독

### 기능 목록

- ✅ 실시간 메시지 전송/수신
- ✅ 사용자 입/퇴장 알림
- ✅ 자동 스크롤
- ✅ 타임스탬프 표시
- ✅ 반응형 디자인
- ✅ 재연결 지원

## 💡 핵심 코드 설명

### Backend - WebSocket 설정

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
```

### Frontend - STOMP 연결

```javascript
const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws-chat'),
  
  onConnect: () => {
    // 메시지 구독
    client.subscribe('/topic/public', (message) => {
      const receivedMessage = JSON.parse(message.body)
      setMessages((prev) => [...prev, receivedMessage])
    })
    
    // 입장 메시지 전송
    client.publish({
      destination: '/app/chat.addUser',
      body: JSON.stringify({ sender: username, type: 'JOIN' })
    })
  }
})

client.activate()
```

## 🎨 UI 특징

- 그라데이션 배경 (보라색 계열)
- 카드 형태의 깔끔한 디자인
- 애니메이션 효과 (메시지 등장)
- 커스텀 스크롤바
- 반응형 레이아웃 (모바일 지원)

## 🔍 테스트 방법

1. 브라우저를 2개 이상 열기
2. 각각 다른 이름으로 채팅방 입장
3. 메시지를 보내면 모든 브라우저에서 실시간으로 확인 가능
4. 입/퇴장 시 시스템 메시지 표시 확인

## 📝 참고사항

- 개발 환경에서는 CORS가 전체 허용되어 있습니다
- 프로덕션 환경에서는 적절한 Origin 설정이 필요합니다
- 메시지는 서버에 저장되지 않습니다 (인메모리)
- 새로고침 시 이전 메시지는 사라집니다

## 🚀 추가 개선 사항 (선택)

- [ ] 메시지 DB 저장
- [ ] 사용자 인증
- [ ] 여러 채팅방 지원
- [ ] 파일 전송
- [ ] 이모지 지원
- [ ] 읽음 표시
- [ ] 타이핑 인디케이터

## 📄 라이센스

MIT License
