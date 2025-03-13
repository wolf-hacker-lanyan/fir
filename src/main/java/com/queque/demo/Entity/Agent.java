package com.queque.demo.Entity;

public class Agent {
    private String agentId;
    private String skill_group_id;
    private int maxAssignedTasks;
    private int currentAssignedTasks;
    private double saturation;
    private boolean isOnline;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getSkill_group_id() {
        return skill_group_id;
    }

    public void setSkill_group_id(String skill_group_id) {
        this.skill_group_id = skill_group_id;
    }

    public int getMaxAssignedTasks() {
        return maxAssignedTasks;
    }

    public void setMaxAssignedTasks(int maxAssignedTasks) {
        this.maxAssignedTasks = maxAssignedTasks;
    }

    public int getCurrentAssignedTasks() {
        return currentAssignedTasks;
    }

    public void setCurrentAssignedTasks(int currentAssignedTasks) {
        this.currentAssignedTasks = currentAssignedTasks;
    }

    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }
}
