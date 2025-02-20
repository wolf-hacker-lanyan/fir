package com.queque.demo.Mapper;

import com.queque.demo.Entity.Permissions;
import com.queque.demo.Entity.SkillGroup;
import com.queque.demo.Entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    // 根据用户ID查询用户
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    // 插入新用户
    @Insert("INSERT INTO users(username, nickname, password, token, status, skill_group_id, maxAssignedTasks, currentAssignedTasks, saturation, isAvailable, shiftStatus ,userType) VALUES(#{username}, #{nickname}, #{password}, #{token}, #{status}, #{skill_group_id}, #{maxAssignedTasks}, #{currentAssignedTasks}, #{saturation}, #{isAvailable}, #{shiftStatus}, #{userType})")
    void insertUser(User user);
    // 更新用户信息
    @Update("UPDATE users SET username = #{username}, nickname=#{nickname}, password = #{password}, token = #{token}, status = #{status},  skill_group_id = #{skill_group_id}, maxAssignedTasks = #{maxAssignedTasks}, currentAssignedTasks = #{currentAssignedTasks}, saturation = #{saturation}, isAvailable = #{isAvailable}, shiftStatus = #{shiftStatus} WHERE id = #{id}")
    void updateUser(User user);

    // 更新用户的登录时间
    @Update("UPDATE users SET lastLoginTime = #{lastLoginTime} WHERE id = #{id}")
    void updateLoginTime(@Param("id") Long id, @Param("lastLoginTime") Timestamp lastLoginTime);

    //获取技能组by id
    @Select("SELECT * FROM skill_groups WHERE id = #{id}")
    SkillGroup findSkillGroupById(Long id);

    //获取全部技能组
    @Select("SELECT * FROM skill_groups")
    List<SkillGroup> findAllSkillGroup();

    //获取权限组by id
    @Select("SELECT * FROM permissions WHERE name = #{name}")
    Permissions findPermissionByName(String name);

    //getUserIdFromToken
    @Select("SELECT id FROM users WHERE token = #{token}")
    Long getUserIdFromToken(String token);
}

