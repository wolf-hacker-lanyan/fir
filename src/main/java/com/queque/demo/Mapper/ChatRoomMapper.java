package com.queque.demo.Mapper;

import com.queque.demo.Entity.ChatRoom;
import com.queque.demo.Entity.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper

public interface ChatRoomMapper {
    @Insert("INSERT INTO chatroom(roomId, state, userid, agentid, type, creattime) VALUES(#{roomId}, #{state}, #{userid}, #{agentid}, #{type}, #{creattime})")
    void insertChatRoom(ChatRoom chatRoom);

    @Select("SELECT * FROM chatroom WHERE roomId = #{roomId}")
    ChatRoom findByRoomId(String roomId);
}
