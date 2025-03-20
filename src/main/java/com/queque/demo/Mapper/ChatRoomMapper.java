package com.queque.demo.Mapper;

import com.queque.demo.Entity.ChatRoom;
import com.queque.demo.Entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatRoomMapper {
    @Insert("INSERT INTO chatroom(roomId, state, userid, agentid, type, creattime,priority) VALUES(#{roomId}, #{state}, #{userid}, #{agentid}, #{type}, #{creattime},#{priority})")
    void insertChatRoom(ChatRoom chatRoom);

    @Select("SELECT * FROM chatroom WHERE roomId = #{roomId}")
    ChatRoom findByRoomId(String roomId);

    //获取全部等待接入的聊天室
    @Select("SELECT * FROM WaitingChatRoom")
    List<ChatRoom> getWaitingChatRoom();

    //指派客服
    @Update("UPDATE chatroom SET agentid = #{agentid} WHERE roomId = #{roomId}")
    void assignAgent(String roomId, String agentid);

    //更新房间状态
    @Update("UPDATE chatroom SET state = #{state} WHERE roomId = #{roomId}")
    void updateRoomState(String roomId, String state);

    //更新房间的客服接入时间
    @Update("UPDATE chatroom SET starttime = #{starttime} WHERE roomId = #{roomId}")
    void updateJoinTime(String roomId, long starttime);

    //更新房间的客服结束时间
    @Update("UPDATE chatroom SET endtime = #{endtime} WHERE roomId = #{roomId}")
    void updateEndTime(String roomId, long endtime);

    //获取全部的聊天室
    @Select("SELECT * FROM chatroom order by priority desc")
    List<ChatRoom> getAllChatRoom();

    //通过用户id获取聊天室
    @Select("SELECT * FROM chatroom WHERE userid = #{userid} order by priority desc")
    List<ChatRoom> getChatRoomByUserId(String userid);

    //通过客服id获取聊天室
    @Select("SELECT * FROM chatroom WHERE agentid = #{agentid} order by priority desc")
    List<ChatRoom> getChatRoomByAgentId(String agentid);

    //通过客服id获取处理中的聊天室
    @Select("SELECT * FROM chatroom WHERE agentid = #{agentid} and state = 'processing'")
    List<ChatRoom> getProcessingChatRoomByAgentId(String agentid);

}
