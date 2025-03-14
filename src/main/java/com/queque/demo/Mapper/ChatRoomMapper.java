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

}
