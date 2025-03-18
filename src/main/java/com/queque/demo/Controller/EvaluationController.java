package com.queque.demo.Controller;

import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Mapper.ChatRoomMapper;
import com.queque.demo.Mapper.EvaluationMapper;
import com.queque.demo.Mapper.UserMapper;
import com.queque.demo.Mapper.VirtualQueueMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationMapper evaluationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ChatRoomMapper chatRoomMapper;

    //发送评论请求
    @PostMapping("/send")
    public ResponseEntity<?> sendEvaluation(HttpServletRequest request, @RequestBody Map requestbody) {
        // 获取用户token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "未提供有效的令牌"));
        }

        String token = authHeader.substring("Bearer ".length());
        if ("null".equals(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        String send_userId = userMapper.getUserIdFromToken(token);
        String agentId = (String) requestbody.get("agentId");
        int score = (int) requestbody.get("score");
        String content = (String) requestbody.get("content");
        LocalDateTime creattime = LocalDateTime.now();

        try {
            evaluationMapper.addEvaluation(send_userId, agentId, score, content, String.valueOf(creattime));
            return ResponseEntity.ok(new ApiResponse<>(1, null, "发送评论成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }

    }

    //获取发送过的评论
    @PostMapping("/get/send")
    public ResponseEntity<?> getSendEvaluation(HttpServletRequest request) {
        // 获取用户token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "未提供有效的令牌"));
        }

        String token = authHeader.substring("Bearer ".length());
        if ("null".equals(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        String send_userId = userMapper.getUserIdFromToken(token);

        try {
            return ResponseEntity.ok(new ApiResponse<>(1, evaluationMapper.getEvaluationBySendUserId(send_userId), "获取发送过的评论成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //获取接收过的评论
    @PostMapping("/get/receive")
    public ResponseEntity<?> getReceiveEvaluation(HttpServletRequest request) {
        // 获取用户token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "未提供有效的令牌"));
        }

        String token = authHeader.substring("Bearer ".length());
        if ("null".equals(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        String receive_userId = userMapper.getUserIdFromToken(token);

        try {
            return ResponseEntity.ok(new ApiResponse<>(1, evaluationMapper.getEvaluationByUserId(receive_userId), "获取收到的评论成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

}
