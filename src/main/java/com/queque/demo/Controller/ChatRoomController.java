package com.queque.demo.Controller;

import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Entity.ChatRoom;
import com.queque.demo.Mapper.ChatRoomMapper;
import com.queque.demo.Mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chatroom")
public class ChatRoomController {
    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private UserMapper userMapper;

    //获取全部的聊天室
    @RequestMapping("/get/all")
    public ResponseEntity<?> sendEvaluation() {
        try {
            //将获取到的聊天室信息中的id获取到用户名
            List<ChatRoom> list=chatRoomMapper.getAllChatRoom();

            for (ChatRoom chatRoom : list) {
                if (chatRoom.getAgentid() == null) {
                    chatRoom.setAgentid("null");
                }else {
                    chatRoom.setAgentid(userMapper.findById(chatRoom.getAgentid()).getUsername());
                }
                if (chatRoom.getUserid() == null) {
                    chatRoom.setUserid("null");
                }else {
                    chatRoom.setUserid(userMapper.findById(chatRoom.getUserid()).getUsername());
                }
            }
            return ResponseEntity.ok(new ApiResponse<>(1,list , "获取成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //获取属于某个客服的聊天室
    @RequestMapping("/get/agent")
    public ResponseEntity<?> getChatRoomByAgentId(@RequestBody Map request) {
        String agentId = request.get("agentId").toString();
        try {
            List<ChatRoom> list=chatRoomMapper.getChatRoomByAgentId(agentId);
            for (ChatRoom chatRoom : list) {
                if (chatRoom.getAgentid() == null) {
                    chatRoom.setAgentid("null");
                }else {
                    chatRoom.setAgentid(userMapper.findById(chatRoom.getAgentid()).getUsername());
                }
                if (chatRoom.getUserid() == null) {
                    chatRoom.setUserid("null");
                }else {
                    chatRoom.setUserid(userMapper.findById(chatRoom.getUserid()).getUsername());
                }
            }
            return ResponseEntity.ok(new ApiResponse<>(1, list, "获取成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //获取属于某个用户的聊天室
    @RequestMapping("/get/user")
    public ResponseEntity<?> getChatRoomByUserId(@RequestBody Map request) {
        String userId = request.get("userId").toString();
        try {
            List<ChatRoom> list=chatRoomMapper.getChatRoomByUserId(userId);
            for (ChatRoom chatRoom : list) {
                if (chatRoom.getAgentid() == null) {
                    chatRoom.setAgentid("null");
                }else {
                    chatRoom.setAgentid(userMapper.findById(chatRoom.getAgentid()).getUsername());
                }
                if (chatRoom.getUserid() == null) {
                    chatRoom.setUserid("null");
                }else {
                    chatRoom.setUserid(userMapper.findById(chatRoom.getUserid()).getUsername());
                }
            }
            return ResponseEntity.ok(new ApiResponse<>(1, list, "获取成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }
}
