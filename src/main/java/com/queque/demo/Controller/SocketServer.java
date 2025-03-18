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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
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
        System.out.println("attributes：" + attributes);
        String token = (String) attributes.get("token");
//        String roomId = (String) attributes.get("roomId");

        if (token == null || !SocketTokenManager.bindSession(token, session)) {
            System.out.println("WebSocket token不合法：" + token);

            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        System.out.println("WebSocket 连接建立：" + token );
//        session.sendMessage(new TextMessage("连接成功，token：" + token));
    }



    // 处理 WebSocket 消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(payload);


        System.out.println("收到消息：" + payload);

        // 处理心跳和系统消息
        if (jsonNode.has("type")) {
            String type = jsonNode.get("type").asText();
            String roomId = jsonNode.get("roomId").asText();
            if ("pong".equals(type)) {
                // 更新用户的活跃状态
                virtualQueueMapper.updateActiveTime(session.getId(), (int) System.currentTimeMillis());
                System.out.println("收到心跳响应from session: " + session.getId());
                return;
            } else if ("ping".equals(type)) {
                // 客户端发送ping，服务端回应pong
                session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
                return;
            } else if ("system".equals(type)) {
                String userId = "system";
                Message message1 = new Message();
                message1.preaseMessage(message.getPayload());
                message1.setUserid(userId);
                message1.setRoomId(roomId);
                System.out.println("message1：" + message1);
                messageMapper.insertMessage(message1);
                return;
            }
        }

        //获取roomid,将消息广播给所有同样roomid的用户
        Map<String, Object> attributes = session.getAttributes();
        String roomId = (String) attributes.get("roomId");
        System.out.println("roomId：" + roomId);
        for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {

            WebSocketSession session1 = entry.getValue();
            System.out.println("遍历session：" + session1);
            Map<String, Object> attributes1 = session1.getAttributes();
            String roomId1 = (String) attributes1.get("roomId");
            if (roomId.equals(roomId1)) {
                session1.sendMessage(new TextMessage(message.getPayload()));
            }
        }

        String token = (String) attributes.get("token");
        Message message1=new Message();
        message1.preaseMessage(message.getPayload());
        String userId = userMapper.getUserIdFromToken(token);
        message1.setUserid(String.valueOf(userId));
        message1.setRoomId(roomId);
        messageMapper.insertMessage(message1);


        // 原本的逻辑
//        String token = (String) attributes.get("token");
////        String roomId = (String) attributes.get("roomId");
//        Message message1=new Message();
//        message1.preaseMessage(message.getPayload());
//        String userId = userMapper.getUserIdFromToken(token);
//        System.out.println("userid：" + userId);
//        message1.setUserid(String.valueOf(userId));
//        Optional<ChatRoom> chatRoom=ChatRoomManager.getByusertoken(token);
//        System.out.println("chatroom：" + chatRoom);
//        message1.setRoomId(chatRoom.get().getRoomId());
//        messageMapper.insertMessage(message1);
//
//
//        session.sendMessage(new TextMessage(message1.toString()));//收到消息



//            sendMessage(token, message.getPayload());

    }

    // 关闭 WebSocket 连接
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 通过 session 找到对应的 token 并移除
        SocketTokenManager.removeSession(session.getId());
        String token = (String) session.getAttributes().get("token");
        String userId = userMapper.getUserIdFromToken(token);
        //记录最后一次活跃时间
        virtualQueueMapper.updateActiveTime(session.getId(), (int) System.currentTimeMillis());
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
//            String token = request.getURI().getQuery();
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                HttpServletRequest httpRequest = servletRequest.getServletRequest();

                String token = httpRequest.getParameter("token");
                String roomId = httpRequest.getParameter("roomId");

                if (SocketTokenManager.isValidToken(token)) {
                    attributes.put("token", token);  // 将 token 存入 attributes
                    attributes.put("roomId", roomId); // 确保 roomId 也存入 attributes
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
