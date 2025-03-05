package com.queque.demo.Mapper;

import com.queque.demo.Entity.Message;
import com.queque.demo.Entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper

public interface MessageMapper {
    @Select("SELECT * FROM chat")
    List<Map> getAllMessage();
    @Insert("INSERT INTO chat(roomId, textContent, msgType, userid, sendTimeTS, sendTime) VALUES(#{roomId}, #{textContent}, #{msgType}, #{userid}, #{sendTimeTS}, #{sendTime})")
    void insertMessage(Message message);
}
