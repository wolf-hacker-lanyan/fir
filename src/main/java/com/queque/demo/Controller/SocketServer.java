package com.queque.demo.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.queque.demo.Entity.ChatRoom;
import com.queque.demo.Entity.ChatRoomManager;
import com.queque.demo.Entity.Message;
import com.queque.demo.Entity.SocketTokenManager;
import com.queque.demo.Mapper.MessageMapper;
import com.queque.demo.Mapper.UserMapper;
import com.queque.demo.Mapper.VirtualQueueMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@EnableWebSocket
public class SocketServer extends TextWebSocketHandler implements WebSocketConfigurer {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private VirtualQueueMapper virtualQueueMapper;
    @PostConstruct
    public void init() {
        System.out.println("WebSocket 服务器已启动...");
    }

    // 注册 WebSocket 端点
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(this, "/chat/socket")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpHandshakeInterceptor()); // 添加自定义拦截器;
    }

    // WebSocket 连接建立时
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        // 获取 token
        Map<String, Object> attributes = session.getAttributes();
        String token = (String) attributes.get("token");

        if (token == null || !SocketTokenManager.bindSession(token, session)) {
            System.out.println("WebSocket token不合法：" + token);

            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        System.out.println("WebSocket 连接建立：" + token);
//        session.sendMessage(new TextMessage("连接成功，token：" + token));
    }



    // 处理 WebSocket 消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(payload);


        System.out.println("收到消息：" + payload);

        // 处理心跳消息
        if (jsonNode.has("type")) {
            String type = jsonNode.get("type").asText();
            if ("pong".equals(type)) {
                // 更新用户的活跃状态
                virtualQueueMapper.updateActiveTime(session.getId(), (int) System.currentTimeMillis());
                System.out.println("收到心跳响应from session: " + session.getId());
                return;
            } else if ("ping".equals(type)) {
                // 客户端发送ping，服务端回应pong
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                return;
            }
        }

        // 原本的逻辑
        Map<String, Object> attributes = session.getAttributes();
        String token = (String) attributes.get("token");
        Message message1=new Message();
        message1.preaseMessage(message.getPayload());
        String userId = userMapper.getUserIdFromToken(token);
        System.out.println("userid：" + userId);
        message1.setUserid(String.valueOf(userId));
        Optional<ChatRoom> chatRoom=ChatRoomManager.getByusertoken(token);
        System.out.println("chatroom：" + chatRoom);
        message1.setRoomId(chatRoom.get().getRoomId());
        messageMapper.insertMessage(message1);


        session.sendMessage(new TextMessage(message1.toString()));//收到消息



//            sendMessage(token, message.getPayload());

    }

    // 关闭 WebSocket 连接
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 通过 session 找到对应的 token 并移除
        SocketTokenManager.removeSession(session.getId());
        System.out.println("WebSocket 连接关闭：" + session.getId());
    }

    // 发送消息给指定 token 的 WebSocketSession
    public static boolean sendMessage(String token, String message) {
        WebSocketSession session = SocketTokenManager.getSession(token);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    // 自定义 HandshakeInterceptor 用于提取查询参数
    public class HttpHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            // 获取查询参数中的 token
            String token = request.getURI().getQuery();
            if (token != null && token.contains("token=")) {
                token = token.split("=")[1];
                // 验证 token 是否有效
//                System.out.println(" token：" + token);

                if (SocketTokenManager.isValidToken(token)) {
                    attributes.put("token", token);  // 将 token 存入 attributes
                    return true;  // token 验证成功，允许连接
                } else {
                    response.setStatusCode(HttpStatus.FORBIDDEN); // 设置为 403 Forbidden
                    return false;  // token 无效，拒绝连接
                }
            }
            response.setStatusCode(HttpStatus.BAD_REQUEST); // 如果没有 token，则返回 400
            return false;  // 没有 token，拒绝连接
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        }
    }

}
