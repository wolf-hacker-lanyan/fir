package com.queque.demo.Controller;

import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Mapper.AgentMapper;
import com.queque.demo.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/get/allocation")
public class AllocationController {
    @Autowired
    private AgentMapper agentMapper;
    private UserMapper userMapper;
    /* ==============
    进入聊天室点击转人工后开始的分配逻辑:
    开始分配
    判断是否优先级高（chatroom的priority）

    如果“优先级高”，则进入下一步判断。
    如果“优先级不高”，则直接进入排队等待（队列）。
    有空闲“同技能组”客服吗？

    如果“有”，直接分配给该客服。
    如果“没有”，继续下一步判断。
    是否允许不同技能组接待？

    如果“不允许”，则进入“排队等待第一个同技能组客服”。
    如果“允许”，继续下一步判断。
    有空闲“不同技能组”客服吗？

    如果“有”，分配给这个可用的客服（跨技能组接待）。
    如果“没有”，则“排队等待第一个空闲客服”。

    ================ */
    @PostMapping("/get/maxtask")
    public ResponseEntity<?> getmaxtask(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String agentid = request.get("agentid").toString();
        try {
            int maxtask = agentMapper.getMaxAssignedTasks(agentid);
            return ResponseEntity.ok(new ApiResponse<>(1, maxtask, "获取最大任务数成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }
}
