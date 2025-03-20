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
import java.util.UUID;

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
        String agentId = requestbody.get("agentId") != null ? requestbody.get("agentId").toString() : null;
        Integer score = requestbody.get("score") != null ? Integer.parseInt(requestbody.get("score").toString()) : null;
        String content = requestbody.get("content") != null ? requestbody.get("content").toString() : null;
        String chatid = UUID.randomUUID().toString();
        if (agentId == null || score == null || content == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "请求参数不完整"));
        }
        LocalDateTime creattime = LocalDateTime.now();

        try {
            evaluationMapper.addEvaluation(chatid,send_userId, agentId, score, content, String.valueOf(creattime));
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

    //获取差评
    @PostMapping("/get/receive/bad")
    public ResponseEntity<?> getReceiveBadEvaluation(HttpServletRequest request) {
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
            return ResponseEntity.ok(new ApiResponse<>(1, evaluationMapper.getBadEvaluationByUserId(receive_userId), "获取收到的差评成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //获取有内容的评论
    @PostMapping("/get/receive/havecontent")
    public ResponseEntity<?> getReceiveContentEvaluation(HttpServletRequest request) {
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
            return ResponseEntity.ok(new ApiResponse<>(1, evaluationMapper.getEvaluationWithFeedbackByUserId(receive_userId), "获取收到的有内容的评论成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

}
