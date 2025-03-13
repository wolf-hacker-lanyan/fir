package com.queque.demo.Controller;

import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Mapper.InfoMapper;
import com.queque.demo.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class InfoController {
    @Autowired
    private InfoMapper infoMapper;
    private UserMapper userMapper;
    //1

    @PostMapping("/agent")
    public ResponseEntity<?> getagentinfo() {
        // 获取客服信息
        try {
            List<Map> agentinfo = infoMapper.getAgentInfo();

            if (agentinfo.isEmpty()) {
                throw new RuntimeException("没有客服");
            }

            return ResponseEntity.ok(new ApiResponse<>(1, agentinfo , "获取客服信息成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }


    @PostMapping("/waituser")
    public ResponseEntity<?> getwaituserinfo() {
        // 通过视图获取等待用户信息
        try {
            return ResponseEntity.ok(new ApiResponse<>(1, infoMapper.getWaitUserInfo(), "获取等待用户信息成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //技能组信息
    @PostMapping("/skillgroup")
    public ResponseEntity<?> getskillgroupinfo() {
        // 获取技能组信息
        try {
            return ResponseEntity.ok(new ApiResponse<>(1, userMapper.getAllSkillGroup(), "获取技能组信息成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

}
