package com.queque.demo.Entity;

import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SocketTokenManager {
    private static final Map<String, WebSocketSession> sessionMap = new HashMap<>();

    // 生成 Token（外部 Server 调用）
    public static String createToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        sessionMap.put(token, null); // 先存 Token，Session 为空
        return token;
    }

    // 绑定 WebSocketSession
    public static boolean bindSession(String token, WebSocketSession session) {
        if (sessionMap.containsKey(token)) {
            sessionMap.put(token, session);
            return true;
        }
        return false;
    }

    // 获取 WebSocketSession
    public static WebSocketSession getSession(String token) {
        return sessionMap.get(token);
    }

    // 关闭 WebSocketSession
    public static void removeSession(String token) {
        sessionMap.remove(token);
    }

    public static boolean isValidToken(String token) {
        return sessionMap.containsKey(token);
    }
}
