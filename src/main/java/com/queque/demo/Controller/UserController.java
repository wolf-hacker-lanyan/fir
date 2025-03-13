package com.queque.demo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Entity.Permissions;
import com.queque.demo.Entity.User;
import com.queque.demo.Mapper.UserMapper;
import com.queque.demo.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // 进行注册逻辑
        try {
            //生成一个token
            user.setToken(java.util.UUID.randomUUID().toString());
            if (user.getUserType() == null) {
                user.setUserType("user");
            }
            userService.registerUser(user);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "注册成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String username = request.get("username").toString();
        String password = request.get("password").toString();
        System.out.println(password);
        try {
            User user = userService.login(username,password);
//            response.put("user", user);
            result.put("id", user.getId());
            result.put("name", user.getUsername());
            result.put("roles", new String[]{user.getUserType()});

            response.put("token", user.getToken());
            response.put("result", result);

            if ("agent".equals(user.getUserType())) {
                //循环遍历技能组ID，查询技能组名称
                response.put("skillgroup_name", userMapper.getSkillGroupById(user.getSkillGroup_id()).getName());
            }
            return ResponseEntity.ok(new ApiResponse<>(1, response, "登录成功"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    @PostMapping("/info")
    public ResponseEntity<?> info(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

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

            // 设置返回数据
            result.put("id", user.getId());
            result.put("name", user.getUsername());
            result.put("roles", new String[]{user.getUserType()});

            // 根据用户类型查询对应的权限组
            Permissions permissionGroup = userMapper.findPermissionByName(user.getUserType());
            if (permissionGroup == null) {
                throw new RuntimeException("用户权限组为空");
            }

            String permissionsJson = permissionGroup.getPermission();
            ObjectMapper objectMapper = new ObjectMapper();

            // 将 JSON 字符串转换为 List<Map<String, String>> 权限列表
            List<Map<String, String>> permissionsList = objectMapper.readValue(
                    permissionsJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)
            );

            // 将权限列表放入结果中
            result.put("permissions", permissionsList);

            // 返回成功响应
            return ResponseEntity.ok(new ApiResponse<>(1, result, "获取用户信息成功"));

        } catch (RuntimeException e) {
            // 自定义业务异常处理
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        } catch (JsonProcessingException e) {
            // JSON 解析异常处理
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(HttpServletRequest request,@RequestBody Map requestbody) {
        String token = extractToken(request);

        if (token == null || token.equals("null")) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        String userId;
        try {
            userId = userMapper.getUserIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "无效的令牌"));
        }

        String oldPassword = requestbody.get("oldPass").toString();
        String newPassword = requestbody.get("newPass").toString();

        System.out.println(oldPassword);
        System.out.println(newPassword);

        try {
            User user = userMapper.findById(userId);
            if (!user.getPassword().equals(oldPassword)) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "密码错误"));
            }
            user.setPassword(newPassword);
            userMapper.updateUser(user);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "修改密码成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }
}

