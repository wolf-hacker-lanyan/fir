package com.queque.demo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.queque.demo.Entity.*;
import com.queque.demo.Mapper.ChatRoomMapper;
import com.queque.demo.Mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatRoomMapper chatRoomMapper;
    //获取用户token的方法
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;  // 未提供有效的令牌
        }

        String token = authHeader.substring("Bearer ".length());
        if ("null".equals(token)) {
            return null;  // 无效的令牌
        }

        return token;
    }
    // 生成 Token
    @PostMapping("/start")
    public ResponseEntity<?> createToken(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        //TODO:整理token验证逻辑
        String token = extractToken(request);
        if (token == null || token.equals("null")) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        // 从 token 中解析出用户ID
        Long userId;
        try {
            userId = userMapper.getUserIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        try {
            // 根据解析出来的 userId 查询用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "用户不存在"));
            }
            //TODO:补充多次创建聊天的判断
            String roomid= String.valueOf(UUID.randomUUID());
            ChatRoomManager.addChatRoom(roomid,token,"");
            ChatRoom chatRoom=new ChatRoom();
            chatRoom.setRoomId(roomid);
            chatRoom.setUserid(String.valueOf(userId));
            chatRoom.setCreattime(new Date().getTime());
            chatRoomMapper.insertChatRoom(chatRoom);
            result.put("token", SocketTokenManager.createToken(token));
            result.put("roomId", roomid);
            // 返回成功响应
            return ResponseEntity.ok(new ApiResponse<>(1, result, "获取sockettoken成功"));

        } catch (RuntimeException e) {
            // 自定义业务异常处理
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }
}
