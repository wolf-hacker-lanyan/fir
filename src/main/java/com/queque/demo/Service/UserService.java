package com.queque.demo.Service;

import com.queque.demo.Entity.User;
import com.queque.demo.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        // 查询数据库用户信息
        User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        // 登录成功，更新登录时间
        user.setLastLoginTime(Timestamp.valueOf(LocalDateTime.now()));
        userMapper.updateLoginTime(user.getUserid(), user.getLastLoginTime());

        return user;
    }


    public void registerUser(User user) {
        User get_user = userMapper.findByUsername(user.getUsername());
        if (get_user != null) {
            System.out.println("get_user");
            throw new RuntimeException("用户名已经存在");
        }
        if ("agent".equals(user.getUserType())) {//user && agent
            //遍历技能组ID，检查技能组是否存在
            System.out.println("遍历技能组ID，检查技能组是否存在");

            if (userMapper.getSkillGroupById(user.getSkillGroup_id()) == null) {
                throw new RuntimeException("提供的技能组有不存在的");
            }

        }

        //将密码加密
        String password_md5 = DigestUtils.sha256Hex(user.getPassword());

        user.setPassword(password_md5);

        //生成一个token
        user.setToken(java.util.UUID.randomUUID().toString());
        user.setUserid(java.util.UUID.randomUUID().toString());

        userMapper.insertUser(user);
    }

}


