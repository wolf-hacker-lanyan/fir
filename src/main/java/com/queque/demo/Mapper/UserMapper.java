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
    @Select("SELECT * FROM users WHERE userid = #{userid}")
    User findById(String userid);

    // 插入新用户
    @Insert("INSERT INTO users(userid,username, nickname, password, token, status, userType) VALUES(#{userid},#{username}, #{nickname}, #{password}, #{token}, #{status}, #{userType})")
    void insertUser(User user);
    // 更新用户信息
    @Update("UPDATE users SET userid = #{userid},username = #{username}, nickname=#{nickname}, password = #{password}, token = #{token}, status = #{status}, isAvailable = #{isAvailable}, shiftStatus = #{shiftStatus} WHERE id = #{id}")
    void updateUser(User user);

    // 更新用户的登录时间
    @Update("UPDATE users SET lastLoginTime = #{lastLoginTime} WHERE userid = #{userid}")
    void updateLoginTime(@Param("userid") String userid, @Param("lastLoginTime") Timestamp lastLoginTime);

    //获取技能组by id
    @Select("SELECT * FROM skill_groups WHERE id = #{id}")
    SkillGroup getSkillGroupById(String id);

    //获取全部技能组
    @Select("SELECT * FROM skill_groups")
    List<SkillGroup> getAllSkillGroup();

    //获取权限组by id
    @Select("SELECT * FROM permissions WHERE name = #{name}")
    Permissions findPermissionByName(String name);

    //getUserIdFromToken
    @Select("SELECT userid FROM users WHERE token = #{token}")
    String getUserIdFromToken(String token);

    //获取全部用户
    @Select("SELECT * FROM users")
    List<User> getAllUser();
}

