package com.queque.demo.Controller;

import com.queque.demo.Entity.Agent;
import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Mapper.AgentMapper;
import com.queque.demo.Mapper.InfoMapper;
import com.queque.demo.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/agent")
public class AgentController {
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private UserMapper userMapper;

    //设置一个用户为客服
    @PostMapping("/add")
    public ResponseEntity<?> addAgent(@RequestBody Map request) {
        String agentid = request.get("userId").toString();
        String skill_group_id = request.get("skillGroupId").toString();
        int maxAssignedTasks = Integer.parseInt(request.get("maxAssignedTasks").toString());//必须大于0
        int currentAssignedTasks = Integer.parseInt(request.get("currentAssignedTasks").toString());//必须大于等于0
        String state = String.valueOf(request.get("state"));

        if (!(agentMapper.getAgentInfoByUserid(agentid)).isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "该用户已经是客服"));
        }

        if (userMapper.getSkillGroupById(skill_group_id) == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "技能组不存在"));
        }

        try {
            agentMapper.addAgent(agentid, skill_group_id, maxAssignedTasks, currentAssignedTasks,false, state);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "添加客服成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAgent(@RequestBody Map request) {
        String agentid = request.get("agentId").toString();
        try {
            agentMapper.deleteAgent(agentid);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "删除客服成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateAgent(@RequestBody Map request) {
        String agentid = request.get("userId").toString();
        String skill_group_id = request.get("skillGroupId").toString();
        int maxAssignedTasks = Integer.parseInt(request.get("maxAssignedTasks").toString());
        int currentAssignedTasks = Integer.parseInt(request.get("currentAssignedTasks").toString());
        String state = request.get("state").toString();
        try {
            agentMapper.updateAgent(agentid, skill_group_id, maxAssignedTasks, currentAssignedTasks, state);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "更新客服信息成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

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

    @PostMapping("/get/currenttask")
    public ResponseEntity<?> getCurrentTask(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String agentid = request.get("agentid").toString();
        try {
            int currenttask = agentMapper.getCurrentAssignedTasks(agentid);
            return ResponseEntity.ok(new ApiResponse<>(1, currenttask, "获取当前任务数成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    @PostMapping("/get/saturation")
    public ResponseEntity<?> getSaturation(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String agentid = request.get("agentid").toString();
        try {
            double saturation = agentMapper.getSaturation(agentid);
            return ResponseEntity.ok(new ApiResponse<>(1, saturation, "获取饱和度成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //更新技能组
    @PostMapping("/change/skillgroup")
    public ResponseEntity<?> changeSkillGroup(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String agentid = request.get("agentid").toString();
        String skill_group_id = request.get("skill_group_id").toString();
        if (userMapper.getSkillGroupById(skill_group_id) == null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, "技能组不存在"));
        }
        try {
            agentMapper.setSkillGroup(agentid, skill_group_id);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "更新技能组成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //更新最大任务数
    @PostMapping("/change/maxtask")
    public ResponseEntity<?> changeMaxTask(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String agentid = request.get("agentid").toString();
        int maxtask = Integer.parseInt(request.get("maxtask").toString());
        try {
            agentMapper.setMaxAssignedTasks(agentid, maxtask);
            changeSaturation(agentid);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "更新最大任务数成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //更新当前任务数
    @PostMapping("/change/currenttask")
    public ResponseEntity<?> changeCurrentTask(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String agentid = request.get("agentid").toString();
        int currenttask = Integer.parseInt(request.get("currenttask").toString());
        try {
            agentMapper.setCurrentAssignedTasks(agentid, currenttask);
            changeSaturation(agentid);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "更新当前任务数成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    //更新饱和度
    public void changeSaturation(String agentid) {
        int CurrentTaskNum = agentMapper.getCurrentAssignedTasks(agentid);
        int MaxTaskNum = agentMapper.getMaxAssignedTasks(agentid);
        double saturation = (double) CurrentTaskNum / MaxTaskNum;
        try {
            agentMapper.setSaturation(agentid, saturation);
            System.out.println("更新饱和度成功");
        } catch (RuntimeException e) {
            System.out.println("更新饱和度失败，" + e.getMessage());
        }
    }

    //获取状态
    public String getState(String agentid) {
        int CurrentTaskNum = agentMapper.getCurrentAssignedTasks(agentid);
        int MaxTaskNum = agentMapper.getMaxAssignedTasks(agentid);
        if (CurrentTaskNum>=MaxTaskNum){
            return "busy";
        }
        else {
            return "idle";
        }
    }


    //更新在线状态
    @PostMapping("/change/state")
    public ResponseEntity<?> changeState(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        Map result = new java.util.HashMap();
        String agentid = request.get("agentid").toString();
        String state = request.get("state").toString();
        try {
            agentMapper.setState(agentid, state);
            return ResponseEntity.ok(new ApiResponse<>(1, null, "更新状态成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }

    @PostMapping("/getall")
    public ResponseEntity<?> getAllAgent() {
        List<Agent> agents = agentMapper.getAllAgent();

        for (Agent agent : agents) {
            //遍历多加一个name
            agent.setName(userMapper.findById(agent.getAgentId()).getUsername());
        }
        return ResponseEntity.ok(new ApiResponse<>(1, agents, "获取全部客服成功"));
    }

}
