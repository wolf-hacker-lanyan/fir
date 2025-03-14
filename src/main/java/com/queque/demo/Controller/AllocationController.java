package com.queque.demo.Controller;

import com.queque.demo.Entity.Agent;
import com.queque.demo.Entity.ApiResponse;
import com.queque.demo.Entity.ChatRoom;
import com.queque.demo.Mapper.*;
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
@RequestMapping("/get/allocation")
public class AllocationController {
    @Autowired
    private AgentMapper agentMapper;
    private ChatRoomMapper chatRoomMapper;
    private UserMapper userMapper;
    private SettingMapper settingMapper;
    private VirtualQueueMapper virtualQueueMapper;

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
    @PostMapping("/allocate")//分配的方法
    public ResponseEntity<?> allocateAgent(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        String userid = request.get("userid").toString();
        boolean allowDifferentSkillGroup = Boolean.parseBoolean(settingMapper.getAllowCrossGroup());
        boolean priorityHighScore = Boolean.parseBoolean(settingMapper.getPriorityHighScore());
        boolean priorityLowSaturation = Boolean.parseBoolean(settingMapper.getPriorityLowSaturation());
        int queueLength = Integer.parseInt(settingMapper.getQueueLength());

        try {
            //获取全部在等待的房间
            List<ChatRoom> RoomList = chatRoomMapper.getWaitingChatRoom();

            //超过设定的人数排队，先进入虚拟排队
            if (RoomList.size()>queueLength){
                virtualQueueMapper.addToVirtualQueue(userid, System.currentTimeMillis());
                return ResponseEntity.ok(new ApiResponse<>(0, null, "已进入排队队列，请耐心等待"));
            }

            // 获取第一个等待的房间
            ChatRoom chatRoom = RoomList.get(0);
            String skillGroupId = chatRoom.getSkill_group_id();
            List<Agent> same_agentList = agentMapper.getFreeAgentInfoBySkillGroupIdOrdByAvgscore(skillGroupId);
            // 有空闲“同技能组”客服吗？
            if (!same_agentList.isEmpty()) {

                //是否评分高的客服优先接待？
                if (priorityHighScore) {
                    // 评分最高的客服
                    Agent agent = same_agentList.get(0);
                    chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent.getAgentId());
                    chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                    System.out.println("成功分配评分最高的客服");
                    return ResponseEntity.ok(new ApiResponse<>(1, null, "分配成功"));
                    }

                //是否饱和度低的客服优先接待？
                if (priorityLowSaturation) {
                    // 评分最高的客服
                    List<Agent> Saturation_agentList = agentMapper.getFreeAgentInfoBySkillGroupIdOrdBySaturation(skillGroupId);
                    Agent agent = Saturation_agentList.get(0);
                    chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent.getAgentId());
                    chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                    System.out.println("成功分配饱和度低的客服");
                    return ResponseEntity.ok(new ApiResponse<>(1, null, "分配成功"));
                }

                //随机分配客服
                int random = (int) (Math.random() * same_agentList.size());
                Agent agent = same_agentList.get(random);
                chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent.getAgentId());
                chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                System.out.println("成功分配随机客服");
                return ResponseEntity.ok(new ApiResponse<>(1, null, "分配成功"));
            }

            // 没有空闲“同技能组”客服
            // 是否允许不同技能组接待
            if (allowDifferentSkillGroup) {
                // 有空闲“不同技能组”客服吗？
                System.out.println("允许不同技能组接待");
                List<Agent> diff_agentList = agentMapper.getFreeAgentInfoOrdByAvgscore();
                if (!diff_agentList.isEmpty()) {
                    //评分高的客服优先接待？
                    if (priorityHighScore) {
                        // 评分最高的客服
                        Agent agent = diff_agentList.get(0);
                        chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent.getAgentId());
                        chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                        System.out.println("成功分配评分最高的客服");
                        return ResponseEntity.ok(new ApiResponse<>(1, null, "分配成功"));
                    }

                    //饱和度低的客服优先接待？
                    if (priorityLowSaturation) {
                        // 评分最高的客服
                        List<Agent> Saturation_agentList = agentMapper.getFreeAgentInfoOrdBySaturation();
                        Agent agent = Saturation_agentList.get(0);
                        chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent.getAgentId());
                        chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                        System.out.println("成功分配饱和度低的客服");
                        return ResponseEntity.ok(new ApiResponse<>(1, null, "分配成功"));
                    }

                    //随机分配客服
                    int random = (int) (Math.random() * diff_agentList.size());
                    Agent agent = diff_agentList.get(random);
                    chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent.getAgentId());
                    chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                    System.out.println("成功分配随机客服");
                    return ResponseEntity.ok(new ApiResponse<>(1, null, "分配成功"));
                }
            }

            // 没有空闲客服，进入排队队列
            chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "waiting");
            return ResponseEntity.ok(new ApiResponse<>(1, null, "已进入排队队列，您是第"+RoomList.size()+"位"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }
}
