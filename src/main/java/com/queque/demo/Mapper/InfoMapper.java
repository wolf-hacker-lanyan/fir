package com.queque.demo.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface InfoMapper {
    @Select("SELECT * FROM agents")
    List<Map> getAgentInfo();

    @Select("SELECT * FROM waitusers")
    List<Map> getWaitUserInfo();
}
