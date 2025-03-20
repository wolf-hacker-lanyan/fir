package com.queque.demo.Mapper;

import com.queque.demo.Entity.Agent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface AgentMapper {
    //获取客服的最大分配任务数
    @Select("SELECT maxAssignedTasks FROM agent WHERE agentId = #{agentId}")
    int getMaxAssignedTasks(String agentId);
    //通过id获取客服
    @Select("SELECT * FROM agent WHERE agentId = #{agentId}")
    Agent getAgentById(String agentId);
    //获取客服的当前分配任务数
    @Select("SELECT currentAssignedTasks FROM agent WHERE agentId = #{agentId}")
    int getCurrentAssignedTasks(String agentId);
    //获取客服的饱和度
    @Select("SELECT saturation FROM agent WHERE agentId = #{agentId}")
    double getSaturation(String agentId);
    //获取客服的状态
    @Select("SELECT state FROM agent WHERE agentId = #{agentId}")
    String getState(String agentId);
    //更改客服的状态
    @Update("UPDATE agent SET state = #{state} WHERE agentId = #{agentId}")
    void setState(String agentId, String state);
    //更改客服的当前分配任务数
    @Update("UPDATE agent SET currentAssignedTasks = #{currentAssignedTasks} WHERE agentId = #{agentId}")
    void setCurrentAssignedTasks(String agentId, int currentAssignedTasks);
    //更改客服的饱和度
    @Update("UPDATE agent SET saturation = #{saturation} WHERE agentId = #{agentId}")
    void setSaturation(String agentId, double saturation);
    //更改客服的最大分配任务数
    @Update("UPDATE agent SET maxAssignedTasks = #{maxAssignedTasks} WHERE agentId = #{agentId}")
    void setMaxAssignedTasks(String agentId, int maxAssignedTasks);
    //插入一个新的客服
    @Insert("INSERT INTO agent (agentId, skill_group_id, maxAssignedTasks, currentAssignedTasks, isOnline, state) VALUES (#{agentId}, #{skill_group_id}, #{maxAssignedTasks}, #{currentAssignedTasks}, #{isOnline}, #{state})")
    void addAgent(String agentId, String skill_group_id, int maxAssignedTasks, int currentAssignedTasks, boolean isOnline, String state);
    @Select("SELECT * FROM agent where agentid = #{agentid}")
    List<Map> getAgentInfoByUserid(String agentid);
    //更改客服的技能组
    @Update("UPDATE agent SET skill_group_id = #{skill_group_id} WHERE agentId = #{agentId}")
    void setSkillGroup(String agentId, String skill_group_id);


    //获取某个技能组的客服(高评价降序)
    @Select("SELECT * FROM agent WHERE skill_group_id = #{skill_group_id} ORDER BY avgscore DESC")
    List<Map> getAgentInfoBySkillGroupIdOrdByAvgscore(String skill_group_id);
    //获取某个技能组的空闲客服(高评价降序)
    @Select("SELECT * FROM agent WHERE skill_group_id = #{skill_group_id} AND state = 'idle' ORDER BY avgscore DESC")
    List<Agent> getFreeAgentInfoBySkillGroupIdOrdByAvgscore(String skill_group_id);
    //获取空闲客服(高评价降序)
    @Select("SELECT * FROM agent WHERE state = 'idle' ORDER BY avgscore DESC")
    List<Agent> getFreeAgentInfoOrdByAvgscore();


    //获取某个技能组的客服(低饱和度优先)
    @Select("SELECT * FROM agent WHERE skill_group_id = #{skill_group_id} ORDER BY saturation ASC")
    List<Map> getAgentInfoBySkillGroupIdOrdBySaturation(String skill_group_id);
    //获取某个技能组的空闲客服(低饱和度优先)
    @Select("SELECT * FROM agent WHERE skill_group_id = #{skill_group_id} AND state = 'idle' ORDER BY saturation ASC")
    List<Agent> getFreeAgentInfoBySkillGroupIdOrdBySaturation(String skill_group_id);
    //获取空闲客服(低饱和度优先)
    @Select("SELECT * FROM agent WHERE state = 'idle' ORDER BY saturation ASC")
    List<Agent> getFreeAgentInfoOrdBySaturation();

}
