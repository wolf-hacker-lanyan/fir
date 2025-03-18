package com.queque.demo.Entity;

public class Evaluation {
    private String id;
    private String chatid;//关联的会话ID(roomid)
    private String userid;
    private String send_userid;
    private int score;//1-5分
    private String feedback;//反馈
    private String creattime;//创建时间（datetime）

    public String getSend_userid() {
        return send_userid;
    }

    public void setSend_userid(String send_userid) {
        this.send_userid = send_userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatid() {
        return chatid;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getCreattime() {
        return creattime;
    }

    public void setCreattime(String creattime) {
        this.creattime = creattime;
    }
}
