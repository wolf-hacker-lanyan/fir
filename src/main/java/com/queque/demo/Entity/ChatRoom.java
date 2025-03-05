package com.queque.demo.Entity;

public class ChatRoom {
    private String roomId;
    private String state;
    private String userid;
    private String agentid;
    private String usertoken;
    private String agenttoken;
    private String type;
    private long starttime;
    private long endtime;
    private long creattime;

    @Override
    public String toString() {
        return "ChatRoom{" +
                "roomId='" + roomId + '\'' +
                ", state='" + state + '\'' +
                ", userid='" + userid + '\'' +
                ", agentid='" + agentid + '\'' +
                ", usertoken='" + usertoken + '\'' +
                ", agenttoken='" + agenttoken + '\'' +
                ", type='" + type + '\'' +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                ", creattime=" + creattime +
                '}';
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public String getUsertoken() {
        return usertoken;
    }

    public void setUsertoken(String usertoken) {
        this.usertoken = usertoken;
    }

    public String getAgenttoken() {
        return agenttoken;
    }

    public void setAgenttoken(String agenttoken) {
        this.agenttoken = agenttoken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getCreattime() {
        return creattime;
    }

    public void setCreattime(long creattime) {
        this.creattime = creattime;
    }
}
