package com.queque.demo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.queque.demo.Entity.*;
import com.queque.demo.Mapper.AgentMapper;
import com.queque.demo.Mapper.ChatRoomMapper;
import com.queque.demo.Mapper.MessageMapper;
import com.queque.demo.Mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SocketServer socketServer;

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
    public ResponseEntity<?> createToken(HttpServletRequest request, @RequestBody Map requestbody) {
        Map<String, Object> result = new HashMap<>();
        //TODO:整理token验证逻辑
        String token = extractToken(request);
        if (token == null || token.equals("null")) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        // 从 token 中解析出用户ID
        String userId;
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

            String roomid;
            if (requestbody.get("roomId") != null) {
                roomid =requestbody.get("roomId").toString();

                if (chatRoomMapper.getChatRoomByUserId(userId) != null) {
                    if (!chatRoomMapper.getChatRoomByUserId(userId).isEmpty()) {
                        for (ChatRoom chatRoom : chatRoomMapper.getChatRoomByUserId(userId)) {
                            if (Objects.equals(chatRoom.getRoomId(), roomid)) {
                                System.out.println("用户进入已有房间，跳过判断");
                                result.put("token", SocketTokenManager.createToken(token));
                                result.put("roomId", roomid);
                                // 返回成功响应
                                return ResponseEntity.ok(new ApiResponse<>(1, result, "用户进入已有房间，跳过判断，获取sockettoken成功"));
                            }
                        }
                    }
                }

                System.out.println(roomid);
            }else {
                if (chatRoomMapper.getChatRoomByUserId(userId) != null) {
                    if (!chatRoomMapper.getChatRoomByUserId(userId).isEmpty()) {
                        for (ChatRoom chatRoom : chatRoomMapper.getChatRoomByUserId(userId)) {
                            if (!chatRoom.getState().equals("done")) {
                                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(1, null, "有一个未结束的会话"));
                            }
                        }
                    }
                }

                roomid= String.valueOf(UUID.randomUUID());
                ChatRoomManager.addChatRoom(roomid,token,"");
                ChatRoom chatRoom=new ChatRoom();
                chatRoom.setRoomId(roomid);
                chatRoom.setUserid(String.valueOf(userId));
                chatRoom.setCreattime(System.currentTimeMillis());
                chatRoom.setState("waiting");
                chatRoomMapper.insertChatRoom(chatRoom);
            }

            result.put("token", SocketTokenManager.createToken(token));
            result.put("roomId", roomid);
            // 返回成功响应
            return ResponseEntity.ok(new ApiResponse<>(1, result, "获取sockettoken成功"));

        } catch (RuntimeException e) {
            // 自定义业务异常处理
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }


    @PostMapping("/info")
    public ResponseEntity<?> getChatroomInfo(@RequestBody Map requestbody) {
        Map<String, Object> result = new HashMap<>();

        try {
            System.out.println(requestbody);
            String roomId = requestbody.get("roomId").toString();
            System.out.println(roomId);
            ChatRoom chatRoom = chatRoomMapper.findByRoomId(roomId);
            System.out.println(chatRoom);
            if (chatRoom == null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "聊天室不存在"));
            }
            User user = userMapper.findById(chatRoom.getUserid());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "用户不存在"));
            }
            String username = user.getUsername();

            String agentname = (chatRoom.getAgentid() != null) ? userMapper.findById(chatRoom.getAgentid()).getUsername() : "未分配";
            String skillgroupname = (chatRoom.getSkill_group_id() != null) ? userMapper.getSkillGroupById(chatRoom.getSkill_group_id()).getName() : "未分配";


            result.put("roomId", chatRoom.getRoomId());
            result.put("username", username);
            result.put("userid", chatRoom.getUserid());
            result.put("agentname", agentname);
            result.put("agentid", chatRoom.getAgentid());
            result.put("creattime", chatRoom.getCreattime());
            result.put("starttime", chatRoom.getStarttime());
            result.put("state", chatRoom.getState());
            result.put("endtime", chatRoom.getEndtime());
            result.put("skillgroupname", skillgroupname);

            //
            System.out.println(result);

            // 返回成功响应
            return ResponseEntity.ok(new ApiResponse<>(1, result, "获取聊天室信息成功"));

        } catch (RuntimeException e) {
            // 自定义业务异常处理
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    @PostMapping("/done")
    public ResponseEntity<?> endChatroom(@RequestBody Map requestbody) {
        Map<String, Object> result = new HashMap<>();

        try {
            String roomId = requestbody.get("roomId").toString();
            ChatRoom chatRoom = chatRoomMapper.findByRoomId(roomId);
            if (chatRoom == null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "聊天室不存在"));
            }
            chatRoomMapper.updateRoomState(roomId, "done");
            chatRoomMapper.updateEndTime(roomId, System.currentTimeMillis());
            agentMapper.setCurrentAssignedTasks(chatRoom.getAgentid(), agentMapper.getCurrentAssignedTasks(chatRoom.getAgentid()) - 1);
            Agent agent = agentMapper.getAgentById(chatRoom.getAgentid());
            if (agent.getCurrentAssignedTasks() < agent.getMaxAssignedTasks()){
                agentMapper.setState(chatRoom.getAgentid(), "idle");
            }

            socketServer.broadcastToRoom(roomId, "{\"type\":\"end\"}");
            // 返回成功响应
            return ResponseEntity.ok(new ApiResponse<>(1, null, "结束聊天成功"));

        } catch (RuntimeException e) {
            // 自定义业务异常处理
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    @PostMapping("/gethistory")
    public ResponseEntity<?> getChatHistory(@RequestBody Map requestbody) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> msg = new HashMap<>();
        try {
            String roomId = requestbody.get("roomId").toString();
            ChatRoom chatRoom = chatRoomMapper.findByRoomId(roomId);
            if (chatRoom == null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "聊天室不存在"));
            }
            List<Message> messages = messageMapper.getMessageByRoomId(roomId);

            for (Message message : messages) {
                msg.put("roomId", message.getRoomId());
                msg.put("userid", message.getUserid());
                msg.put("msgType", message.getMsgType());
                msg.put("textContent", message.getTextContent());
            }
            // 返回成功响应
            return ResponseEntity.ok(new ApiResponse<>(1, messages, "获取聊天记录成功"));

        } catch (RuntimeException e) {
            // 自定义业务异常处理
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }
}
