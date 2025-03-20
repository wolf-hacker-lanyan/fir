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
    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SettingMapper settingMapper;
    @Autowired
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
    @PostMapping("/allocate")//排队的方法
    public ResponseEntity<?> allocateAgent(@RequestBody Map request) {
        Map response = new java.util.HashMap();
        String userid = request.get("userid").toString();
        String roomid = request.get("roomid").toString();
        int queueLength = Integer.parseInt(settingMapper.getQueueLength());

        try {
            //获取全部在等待的房间
            List<ChatRoom> RoomList = chatRoomMapper.getWaitingChatRoom();

            if (RoomList.size() == 1) {
                return ResponseEntity.ok(new ApiResponse<>(1, null, "分配成功"));
            }


            //超过设定的人数排队，先进入虚拟排队
            if (RoomList.size()>queueLength){
                virtualQueueMapper.addToVirtualQueue(userid, System.currentTimeMillis());
                return ResponseEntity.ok(new ApiResponse<>(1, null, "已进入排队队列，请耐心等待"));
            }

            return ResponseEntity.ok(new ApiResponse<>(1, null, "已进入排队队列，您是第"+RoomList.size()+"位"));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(0, null, e.getMessage()));
        }
    }
}
