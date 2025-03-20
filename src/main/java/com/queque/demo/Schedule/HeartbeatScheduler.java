package com.queque.demo.Schedule;

import com.queque.demo.Controller.SocketServer;
import com.queque.demo.Entity.*;
import com.queque.demo.Mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class HeartbeatScheduler {
    WebSocketSession session;
    @Autowired
    private VirtualQueueMapper virtualQueueMapper;
    @Autowired
    private SettingMapper settingMapper;
    @Autowired
    private ChatRoomMapper chatRoomMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AgentMapper agentMapper;
    @Autowired
    private SocketServer socketServer;

    //定期发送心跳
    @Scheduled(fixedRate = 30000)//30s
    public void sendHeartbeat() {
        System.out.println("心跳检测");
        for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage("{\"type\":\"ping\"}"));
                } catch (IOException e) {
                    e.printStackTrace();
                    SocketTokenManager.removeSession(session.getId());
                }
            }
        }
    }
    //定期检查心跳，超时则踢出
    @Scheduled(fixedRate = 30000)
    public void checkHeartbeat() {
        System.out.println("超时检测");
        int time = Integer.parseInt(settingMapper.getTimeoutTime()) * 60000;
        long now = System.currentTimeMillis();
        for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen() && session != null) {
                Map<String, Object> attributes = session.getAttributes();
                String token = (String) attributes.get("token");
                Optional<ChatRoom> chatRoom= ChatRoomManager.getByusertoken(token);
                String userId = userMapper.getUserIdFromToken(token);
                Long lastActiveTimeObj = virtualQueueMapper.getLastActiveTime(userId);
                long lastActiveTime = (lastActiveTimeObj != null) ? lastActiveTimeObj : 0L;
                // 如果是0L，说明用户没有进入虚拟队列，不需要检测
                if (lastActiveTime == 0L) {
                    continue;
                }
                if (now - lastActiveTime > time) {
                    try {
                        session.sendMessage(new TextMessage("{\"type\":\"timeout\"}"));
                        System.out.println("触发超时 userid：" + userId);
                        virtualQueueMapper.leaveVirtualQueue(userId);
                        SocketTokenManager.removeSession(session.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                        SocketTokenManager.removeSession(session.getId());
                    }
                }
            }
        }
    }
    //定期检测实时队列人数，有位置退出虚拟队列移入实时队列
    @Scheduled(fixedRate = 30000)
    public void checkQueue() {
        int max = Integer.parseInt(settingMapper.getQueueLength());
        int current = chatRoomMapper.getWaitingChatRoom().size();
        if (current < max) {
            for (Map.Entry<String, WebSocketSession> entry : SocketTokenManager.sessionMap.entrySet()) {
                WebSocketSession session = entry.getValue();
                Map<String, Object> attributes = session.getAttributes();
                String token = (String) attributes.get("token");
                Optional<ChatRoom> chatRoom= ChatRoomManager.getByusertoken(token);
                String userId = userMapper.getUserIdFromToken(token);
                if (session != null && session.isOpen()) {
                    try {
                        //先检测用户是否在虚拟队列
                        if (virtualQueueMapper.findUser(userId)==null) {
                            continue;
                        }
                        session.sendMessage(new TextMessage("{\"type\":\"leaveVirtualQueue\"}"));
                        System.out.println("触发进入实时队列：" + userId);
                        virtualQueueMapper.leaveVirtualQueue(userId);
                    } catch (IOException e) {
                        e.printStackTrace();
                        SocketTokenManager.removeSession(session.getId());
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 10000)
    public void allocateAgent() {
        Map response = new java.util.HashMap();
        boolean allowDifferentSkillGroup = Boolean.parseBoolean(settingMapper.getAllowCrossGroup());
        boolean priorityHighScore = Boolean.parseBoolean(settingMapper.getPriorityHighScore());
        boolean priorityLowSaturation = Boolean.parseBoolean(settingMapper.getPriorityLowSaturation());
        int queueLength = Integer.parseInt(settingMapper.getQueueLength());

        if (!priorityHighScore && !priorityLowSaturation){
            System.out.println("优先接待条件未设置");
            return;
        }

        try {
            //获取全部在等待的房间
            List<ChatRoom> RoomList = chatRoomMapper.getWaitingChatRoom();
            if (RoomList.isEmpty()) {
                System.out.println("没有需要分配的房间");
                return;
            }

            // 获取第一个等待的房间
            ChatRoom chatRoom = RoomList.get(0);
            String skillGroupId = chatRoom.getSkill_group_id();
            List<Agent> same_agentList = agentMapper.getFreeAgentInfoBySkillGroupIdOrdByAvgscore(skillGroupId);
            // 有空闲“同技能组”客服吗？
            if (!same_agentList.isEmpty()) {

                //是否评分高的客服优先接待？
                if (priorityHighScore) {
                    //判断这个客服和会话的用户是不是一个人，如果是的话遍历直到第一个不是一个人的客服
                    Agent agent_1 = null;// 评分最高的客服

                    for (Agent agent : same_agentList) {
                        if (!agent.getAgentId().equals(chatRoom.getUserid())) {
                            agent_1 = agent;
                            break;
                        }
                    }

                    if (agent_1==null){
                        System.out.println("没有空闲同技能组客服或者剩余客服与用户是同一个人");
                    }

                    chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent_1.getAgentId());
                    chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                    chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
                    agentMapper.setCurrentAssignedTasks(agent_1.getAgentId(), agentMapper.getCurrentAssignedTasks(agent_1.getAgentId()) + 1);
                    if (agent_1.getCurrentAssignedTasks() > agent_1.getMaxAssignedTasks()) {
                        {
                            agentMapper.setState(agent_1.getAgentId(), "busy");
                        }
                    }
                    socketServer.broadcastToRoom(chatRoom.getRoomId(), "{\"type\":\"allocate-success\"}");
                    System.out.println("成功分配评分最高的客服");
                    return;
                }


                //是否饱和度低的客服优先接待？
                if (priorityLowSaturation) {
                    // 评分最高的客服
                    List<Agent> Saturation_agentList = agentMapper.getFreeAgentInfoBySkillGroupIdOrdBySaturation(skillGroupId);
                    Agent agent_2 = null;
                    for (Agent agent : Saturation_agentList) {
                        if (!agent.getAgentId().equals(chatRoom.getUserid())) {
                            agent_2 = agent;
                            break;
                        }
                    }

                    if (agent_2==null){
                        System.out.println("没有空闲同技能组客服或者剩余客服与用户是同一个人");
                    }
                    chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent_2.getAgentId());
                    chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                    chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
                    agentMapper.setCurrentAssignedTasks(agent_2.getAgentId(), agentMapper.getCurrentAssignedTasks(agent_2.getAgentId()) + 1);

                    if (agent_2.getCurrentAssignedTasks() > agent_2.getMaxAssignedTasks()) {
                        {
                            agentMapper.setState(agent_2.getAgentId(), "busy");
                        }
                    }
                    socketServer.broadcastToRoom(chatRoom.getRoomId(), "{\"type\":\"allocate-success\"}");
                    chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
                    System.out.println("成功分配饱和度低的客服");
                    return;
                }

//                //随机分配客服
//                int random = (int) (Math.random() * same_agentList.size());
//                Agent agent_3 = same_agentList.get(random);
//                chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent_3.getAgentId());
//                chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
//                chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
//                agentMapper.setCurrentAssignedTasks(agent_3.getAgentId(), agentMapper.getCurrentAssignedTasks(agent_3.getAgentId()) + 1);
//
//                if (agent_3.getCurrentAssignedTasks() == agent_3.getMaxAssignedTasks()) {
//                    agentMapper.setState(agent_3.getAgentId(), "busy");
//                }
//                socketServer.broadcastToRoom(chatRoom.getRoomId(), "{\"type\":\"allocate-success\"}");
//                chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
//                System.out.println("成功分配随机客服");
//                return;
            }


            System.out.println("没有空闲同技能组客服");

            // 没有空闲“同技能组”客服
            // 是否允许不同技能组接待
            System.out.println("是否允许不同技能组接待=" + allowDifferentSkillGroup);
            if (allowDifferentSkillGroup) {
                // 有空闲“不同技能组”客服吗？
                System.out.println("允许不同技能组接待");
                List<Agent> diff_agentList = agentMapper.getFreeAgentInfoOrdByAvgscore();
                System.out.println("diff_agentList: " + diff_agentList);
                if (!diff_agentList.isEmpty()) {
                    //评分高的客服优先接待？
                    if (priorityHighScore) {
                        // 评分最高的客服
                        Agent agent_4 = null;
                        for (Agent agent : diff_agentList) {
                            if (!agent.getAgentId().equals(chatRoom.getUserid())) {
                                agent_4 = agent;
                                break;
                            }
                        }

                        if (agent_4==null){
                            System.out.println("没有空闲同技能组客服或者剩余客服与用户是同一个人");
                        }
                        chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent_4.getAgentId());
                        chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                        chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
                        agentMapper.setCurrentAssignedTasks(agent_4.getAgentId(), agentMapper.getCurrentAssignedTasks(agent_4.getAgentId()) + 1);

                        if (agent_4.getCurrentAssignedTasks() > agent_4.getMaxAssignedTasks()) {
                            {
                                agentMapper.setState(agent_4.getAgentId(), "busy");
                            }
                        }
                        socketServer.broadcastToRoom(chatRoom.getRoomId(), "{\"type\":\"allocate-success\"}");
                        chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
                        System.out.println("成功分配评分最高的客服");
                        return;
                    }

                    //饱和度低的客服优先接待？
                    if (priorityLowSaturation) {
                        // 评分最高的客服
                        List<Agent> Saturation_agentList = agentMapper.getFreeAgentInfoOrdBySaturation();
                        Agent agent_5 = null;
                        for (Agent agent : Saturation_agentList) {
                            if (!agent.getAgentId().equals(chatRoom.getUserid())) {
                                agent_5 = agent;
                                break;
                            }
                        }

                        if (agent_5==null){
                            System.out.println("没有空闲同技能组客服或者剩余客服与用户是同一个人");
                        }
                        chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent_5.getAgentId());
                        chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
                        chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
                        agentMapper.setCurrentAssignedTasks(agent_5.getAgentId(), agentMapper.getCurrentAssignedTasks(agent_5.getAgentId()) + 1);

                        if (agent_5.getCurrentAssignedTasks() > agent_5.getMaxAssignedTasks()) {
                            {
                                agentMapper.setState(agent_5.getAgentId(), "busy");
                            }
                        }
                        socketServer.broadcastToRoom(chatRoom.getRoomId(), "{\"type\":\"allocate-success\"}");
                        chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
                        System.out.println("成功分配饱和度低的客服");
                        return;
                    }

                    //随机分配客服
//                    int random = (int) (Math.random() * diff_agentList.size());
//                    Agent agent_6 = diff_agentList.get(random);
//                    chatRoomMapper.assignAgent(chatRoom.getRoomId(), agent_6.getAgentId());
//                    chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "processing");
//                    chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
//                    agentMapper.setCurrentAssignedTasks(agent_6.getAgentId(), agentMapper.getCurrentAssignedTasks(agent_6.getAgentId()) + 1);
//
//                    if (agent_6.getCurrentAssignedTasks() > agent_6.getMaxAssignedTasks()) {
//                        {
//                            agentMapper.setState(agent_6.getAgentId(), "busy");
//                        }
//                    }
//                    socketServer.broadcastToRoom(chatRoom.getRoomId(), "{\"type\":\"allocate-success\"}");
//                    chatRoomMapper.updateJoinTime(chatRoom.getRoomId(), System.currentTimeMillis());
//                    System.out.println("成功分配随机客服");
//                    return;
                }
            }

            System.out.println("没有空闲客服");

            // 没有空闲客服，进入排队队列
//            chatRoomMapper.updateRoomState(chatRoom.getRoomId(), "waiting");

        } catch (RuntimeException e) {
            System.out.println("分配客服失败，" + e.getMessage());
        }
    }


}

