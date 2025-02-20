package com.queque.demo.Entity;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

public class User {
    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String token;
    private String status;
    private Timestamp lastLoginTime;
    private String userType;  // "user" 或 "servicer"
    private String skill_group_id;  // 用于skillGroup_id 外键关联 skill_groups 表
    private Integer maxAssignedTasks;
    private Integer currentAssignedTasks;
    private Double saturation;
    private Boolean isAvailable;
    private String shiftStatus;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSkill_group_id() {
        return skill_group_id;
    }

    public void setSkill_group_id(String skill_group_id) {
        this.skill_group_id = skill_group_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


    public String getSkillGroup_id() {
        return skill_group_id;
    }

    public void setSkillGroup_id(String skill_group_id) {
        this.skill_group_id = skill_group_id;
    }

    public Integer getMaxAssignedTasks() {
        return maxAssignedTasks;
    }

    public void setMaxAssignedTasks(Integer maxAssignedTasks) {
        this.maxAssignedTasks = maxAssignedTasks;
    }

    public Integer getCurrentAssignedTasks() {
        return currentAssignedTasks;
    }

    public void setCurrentAssignedTasks(Integer currentAssignedTasks) {
        this.currentAssignedTasks = currentAssignedTasks;
    }

    public Double getSaturation() {
        return saturation;
    }

    public void setSaturation(Double saturation) {
        this.saturation = saturation;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getShiftStatus() {
        return shiftStatus;
    }

    public void setShiftStatus(String shiftStatus) {
        this.shiftStatus = shiftStatus;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", status='" + status + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", userType='" + userType + '\'' +
                ", skillGroup_id=" + skill_group_id +
                ", maxAssignedTasks=" + maxAssignedTasks +
                ", currentAssignedTasks=" + currentAssignedTasks +
                ", saturation=" + saturation +
                ", isAvailable=" + isAvailable +
                ", shiftStatus='" + shiftStatus + '\'' +
                '}';
    }
}
