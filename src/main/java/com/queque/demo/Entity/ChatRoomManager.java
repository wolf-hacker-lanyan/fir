package com.queque.demo.Entity;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Component
public class ChatRoomManager {
    private static final List<ChatRoom> chatRooms = new ArrayList<>();
    // 添加房间信息
    public static void addChatRoom(String roomId, String usertoken, String agenttoken) {
        ChatRoom chatRoom =new ChatRoom();
        chatRoom.setRoomId(roomId);
        chatRoom.setUsertoken(usertoken);
        chatRoom.setAgenttoken(agenttoken);
        chatRooms.add(chatRoom);
    }
    // 通过 roomId 获取 userId 和 agentId
    public static Optional<ChatRoom> getByusertoken(String usertoken) {
        return chatRooms.stream()
                .filter(entry -> entry.getUsertoken().equals(usertoken))
                .findFirst();
    }

    // 通过 userId 获取 roomId 和 agentId
    public static Optional<ChatRoom> getByUserId(String userid) {
        return chatRooms.stream()
                .filter(entry -> userid != null && userid.equals(entry.getUserid()))
                .findFirst();
    }

    // 通过 agentId 获取 roomId 和 userId
    public Optional<ChatRoom> getByAgentId(String agentId) {
        return chatRooms.stream()
                .filter(entry -> entry.getAgentid().equals(agentId))
                .findFirst();
    }

    // 通过 type 获取 roomId
    public static Optional<ChatRoom> getByType(String type) {
        return chatRooms.stream()
                .filter(entry -> entry.getType().equals(type))
                .findFirst();
    }

    // 移除房间信息
    public boolean removeByRoomId(String roomId) {
        return chatRooms.removeIf(entry -> entry.getRoomId().equals(roomId));
    }
}
