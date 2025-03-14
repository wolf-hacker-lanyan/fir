package com.queque.demo.Mapper;

import com.queque.demo.Entity.VirtualQueue;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VirtualQueueMapper {
    //进入虚拟队列
    @Insert("INSERT INTO virtual_queue(userid,jointime) VALUES(#{userid},#{jointime})")
    void addToVirtualQueue(String userid, long jointime);
    //获取虚拟队列
    @Select("SELECT * FROM virtual_queue order by jointime asc ")
    List<VirtualQueue> getVirtualQueue();
    //离开虚拟队列
    @Delete("DELETE FROM virtual_queue WHERE userid = #{userid}")
    void leaveVirtualQueue(String userid);
    //更新活跃时间
    @Update("UPDATE virtual_queue SET lastactive = #{lastactive} WHERE userid = #{userid}")
    void updateActiveTime(String userid, int lastactive);
    //将超时用户移出队列
    @Delete("DELETE FROM virtual_queue WHERE lastactive < #{time}")
    void removeTimeoutUser(int time);
}
