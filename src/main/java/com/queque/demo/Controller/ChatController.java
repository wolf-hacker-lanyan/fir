package com.queque.demo.Controller;

import com.queque.demo.Entity.SocketTokenManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    // 生成 Token
    @PostMapping("/start")
    public Map<String, String> createToken() {
        Map<String, String> response = new HashMap<>();
        response.put("token", SocketTokenManager.createToken());
        return response;
    }
}
