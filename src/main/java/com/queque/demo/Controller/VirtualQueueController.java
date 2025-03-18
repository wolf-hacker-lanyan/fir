package com.queque.demo.Controller;

import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Mapper.UserMapper;
import com.queque.demo.Mapper.VirtualQueueMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/queue")
public class VirtualQueueController {
    @Autowired
    private VirtualQueueMapper virtualQueueMapper;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/updatetime")
    public ResponseEntity<?> UpdateTime(HttpServletRequest request,@RequestBody Map requestbody) {
        // 获取用户token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "未提供有效的令牌"));
        }

        String token = authHeader.substring("Bearer ".length());
        if ("null".equals(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        //更新活跃时间
        String userId=userMapper.getUserIdFromToken(token);
        long time=(long)requestbody.get("time");
        if (virtualQueueMapper.findUser(userId)==null){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(1, null, "用户不在队列中"));
        }
        try {
            virtualQueueMapper.updateActiveTime(userId,time);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "更新成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

}
