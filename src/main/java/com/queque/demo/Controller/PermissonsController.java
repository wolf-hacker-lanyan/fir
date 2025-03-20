package com.queque.demo.Controller;

import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Entity.User;
import com.queque.demo.Mapper.PermissionsMapper;
import com.queque.demo.Mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/permissions")
public class PermissonsController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PermissionsMapper permissionsMapper;

    @PostMapping("/add")
    public ResponseEntity<?> addPermissons(@RequestBody Map requestBody) {
        String name =requestBody.get("name").toString();
        String description = requestBody.get("description")==null?null:requestBody.get("description").toString();
        String permission = requestBody.get("permission").toString();
        permissionsMapper.addPermissions(name,description,permission);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(1, null, "添加成功"));
    }

    @PostMapping("/get")
    public ResponseEntity<?> GetPermissons() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(1, permissionsMapper.getPermissions(), "获取成功"));
    }

    @PostMapping("/update")
    public ResponseEntity<?> UpdatePermissons(@RequestBody Map requestBody) {
        String name =requestBody.get("name").toString();
        String description = requestBody.get("description")==null?null:requestBody.get("description").toString();
        String permission = requestBody.get("permission").toString();
        permissionsMapper.updatePermissions(name,description,permission);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(1, null, "修改成功"));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> DeletePermissons(@RequestBody Map requestBody) {
        String name =requestBody.get("name").toString();
        permissionsMapper.deletePermissions(name);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(1, null, "删除成功"));
    }

    @PostMapping("/getallname")
    public ResponseEntity<?> GetPermissonsName() {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(1, permissionsMapper.getPermissionName(), "获取成功"));
    }

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
}
