package com.queque.demo.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SettingMapper {
    //获取是否允许不同技能组接待
    @Select("SELECT value FROM settings WHERE settingname = 'allowCrossGroup'")
    String getAllowCrossGroup();
    //获取是否评分高的客服优先接待
    @Select("SELECT value FROM settings WHERE settingname = 'priorityHighScore'")
    String getPriorityHighScore();
    //获取是否饱和度低的客服优先接待
    @Select("SELECT value FROM settings WHERE settingname = 'priorityLowSaturation'")
    String getPriorityLowSaturation();
    //获取队列长度
    @Select("SELECT value FROM settings WHERE settingname = 'queueLength'")
    String getQueueLength();
    //获取超时时间
    @Select("SELECT value FROM settings WHERE settingname = 'timeoutTime'")
    String getTimeoutTime();
}
