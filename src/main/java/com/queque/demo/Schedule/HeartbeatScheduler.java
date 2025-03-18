package com.queque.demo.Schedule;

import com.queque.demo.Entity.ChatRoom;
import com.queque.demo.Entity.ChatRoomManager;
import com.queque.demo.Entity.Message;
import com.queque.demo.Entity.SocketTokenManager;
import com.queque.demo.Mapper.ChatRoomMapper;
import com.queque.demo.Mapper.SettingMapper;
import com.queque.demo.Mapper.UserMapper;
import com.queque.demo.Mapper.VirtualQueueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class HeartbeatScheduler {
    @Autowired
    private VirtualQueueMapper virtualQueueMapper;
    @Autowired
    private SettingMapper settingMapper;
    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private UserMapper userMapper;

    WebSocketSession session;


    //定期发送心跳
    @Scheduled(fixedRate = 30000)//30s
    public void sendHeartbeat() {
        System.out.println("心跳检测");
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
    //定期检查心跳，超时则踢出
    @Scheduled(fixedRate = 30000)
    public void checkHeartbeat() {
        System.out.println("超时检测");
        int time = Integer.parseInt(settingMapper.getTimeoutTime()) * 60000;
        long now = System.currentTimeMillis();
        for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen() && session != null) {
                Map<String, Object> attributes = session.getAttributes();
                String token = (String) attributes.get("token");
                Optional<ChatRoom> chatRoom= ChatRoomManager.getByusertoken(token);
                String userId = userMapper.getUserIdFromToken(token);
                Long lastActiveTimeObj = virtualQueueMapper.getLastActiveTime(userId);
                long lastActiveTime = (lastActiveTimeObj != null) ? lastActiveTimeObj : 0L;
                // 如果是0L，说明用户没有进入虚拟队列，不需要检测
                if (lastActiveTime == 0L) {
                    continue;
                }
                if (now - lastActiveTime > time) {
                    try {
                        session.sendMessage(new TextMessage("{\"type\":\"timeout\"}"));
                        System.out.println("触发超时 userid：" + userId);
                        virtualQueueMapper.leaveVirtualQueue(userId);
                        SocketTokenManager.removeSession(session.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                        SocketTokenManager.removeSession(session.getId());
                    }
                }
            }
        }
    }
    //定期检测实时队列人数，有位置退出虚拟队列移入实时队列
    @Scheduled(fixedRate = 30000)
    public void checkQueue() {
        int max = Integer.parseInt(settingMapper.getQueueLength());
        int current = chatRoomMapper.getWaitingChatRoom().size();
        if (current < max) {
            for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {
                WebSocketSession session = entry.getValue();
                Map<String, Object> attributes = session.getAttributes();
                String token = (String) attributes.get("token");
                Optional<ChatRoom> chatRoom= ChatRoomManager.getByusertoken(token);
                String userId = userMapper.getUserIdFromToken(token);
                if (session != null && session.isOpen()) {
                    try {
                        //先检测用户是否在虚拟队列
                        if (virtualQueueMapper.findUser(userId)==null) {
                            continue;
                        }
                        session.sendMessage(new TextMessage("{\"type\":\"leaveVirtualQueue\"}"));
                        System.out.println("触发进入实时队列：" + userId);
                        virtualQueueMapper.leaveVirtualQueue(userId);
                    } catch (IOException e) {
                        e.printStackTrace();
                        SocketTokenManager.removeSession(session.getId());
                    }
                }
            }
        }
    }


}

