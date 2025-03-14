package com.queque.demo.Schedule;

import com.queque.demo.Entity.SocketTokenManager;
import com.queque.demo.Mapper.SettingMapper;
import com.queque.demo.Mapper.VirtualQueueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Component
public class HeartbeatScheduler {
    @Autowired
    private VirtualQueueMapper virtualQueueMapper;
    private SettingMapper settingMapper;

    WebSocketSession session;

    @Scheduled(fixedRate = 30000)//30s
    public void sendHeartbeat() {
        for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage("{\"type\":\"ping\"}"));
                } catch (IOException e) {
                    e.printStackTrace();
                    SocketTokenManager.removeSession(session.getId());
                }
            }
        }
    }

    @Scheduled(fixedRate = 30000)
    public void checkHeartbeat() {
        int time = Integer.parseInt(settingMapper.getTimeoutTime()) * 60000;
        long now = System.currentTimeMillis();
        for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session != null && session.isOpen()) {
                long lastActiveTime = Long.parseLong(session.getAttributes().get("lastActiveTime").toString());
                if (now - lastActiveTime > time) {
                    try {
                        session.sendMessage(new TextMessage("{\"type\":\"timeout\"}"));
                        virtualQueueMapper.leaveVirtualQueue(session.getAttributes().get("userid").toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        SocketTokenManager.removeSession(session.getId());
                    }
                }
            }
        }
    }


}

