import { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

function App() {
  const [username, setUsername] = useState('');
  const [isConnected, setIsConnected] = useState(false);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [stompClient, setStompClient] = useState(null);

  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 채팅 입장
  const handleLogin = (e) => {
    e.preventDefault();

    if (!username.trim()) {
      alert('이름을 입력해주세요!');
      return;
    }

    // STOMP 클라이언트 생성
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws-chat'),

      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,

      onConnect: () => {
        console.log('WebSocket 연결 성공!');
        setIsConnected(true);

        // /topic/public 구독
        client.subscribe('/topic/public', (message) => {
          const receivedMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, receivedMessage]);
        });

        // 입장 메시지 전송
        client.publish({
          destination: '/app/chat.addUser',
          body: JSON.stringify({
            sender: username,
            type: 'JOIN',
          }),
        });
      },

      onStompError: (frame) => {
        console.error('STOMP 에러:', frame);
        alert('연결에 실패했습니다.');
      },

      onWebSocketError: (error) => {
        console.error('WebSocket 에러:', error);
      },

      onDisconnect: () => {
        console.log('WebSocket 연결 종료');
        setIsConnected(false);
      },
    });

    client.activate();
    setStompClient(client);
  };

  // 메시지 전송
  const handleSendMessage = (e) => {
    e.preventDefault();

    if (!inputMessage.trim()) return;

    if (stompClient && stompClient.connected) {
      const chatMessage = {
        sender: username,
        content: inputMessage,
        type: 'CHAT',
      };

      stompClient.publish({
        destination: '/app/chat.sendMessage',
        body: JSON.stringify(chatMessage),
      });

      setInputMessage('');
    }
  };

  // 채팅 나가기
  const handleLeave = () => {
    if (stompClient) {
      stompClient.deactivate();
    }
    setIsConnected(false);
    setMessages([]);
    setUsername('');
  };

  // 컴포넌트 언마운트 시 연결 해제
  useEffect(() => {
    return () => {
      if (stompClient) {
        stompClient.deactivate();
      }
    };
  }, [stompClient]);

  // 로그인 화면
  if (!isConnected) {
    return (
      <div className="login-container">
        <h1>💬 WebSocket 채팅</h1>
        <form onSubmit={handleLogin}>
          <input
            type="text"
            placeholder="이름을 입력하세요"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            maxLength={20}
          />
          <button type="submit">입장하기</button>
        </form>
      </div>
    );
  }

  // 채팅 화면
  return (
    <div className="chat-container">
      <div className="chat-header">
        <h2>💬 채팅방</h2>
        <button className="leave-btn" onClick={handleLeave}>
          나가기
        </button>
      </div>

      <div className="messages-container">
        {messages.map((msg, index) => {
          if (msg.type === 'JOIN' || msg.type === 'LEAVE') {
            return (
              <div key={index} className="message system">
                {msg.content}
              </div>
            );
          }

          return (
            <div key={index} className="message chat">
              <div className="message-sender">{msg.sender}</div>
              <div className="message-content">{msg.content}</div>
              <div className="message-time">{msg.timestamp}</div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>

      <form className="message-input-container" onSubmit={handleSendMessage}>
        <input
          type="text"
          className="message-input"
          placeholder="메시지를 입력하세요..."
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          maxLength={500}
        />
        <button
          type="submit"
          className="send-btn"
          disabled={!inputMessage.trim()}
        >
          전송
        </button>
      </form>
    </div>
  );
}

export default App;
