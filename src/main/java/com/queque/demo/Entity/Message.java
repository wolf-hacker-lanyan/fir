package com.queque.demo.Entity;

import java.util.Date;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
    private String roomId;
    private String textContent;
    private String msgType;
    private String userid;
    private Date sendTime;
    private long sendTimeTS;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public long getSendTimeTS() {
        return sendTimeTS;
    }

    public void setSendTimeTS(long sendTimeTS) {
        this.sendTimeTS = sendTimeTS;
    }

    @Override
    public String toString() {
        return "{" +
                "\"roomId\":\"" + roomId + "\"," +
                "\"textContent\":\"" + textContent + "\"," +
                "\"msgType\":\"" + msgType + "\"," +
                "\"userid\":\"" + userid + "\"," +
                "\"sendTime\":\"" + sendTime.toString() + "\"," +
                "\"sendTimeTS\":" + sendTimeTS +
                "}";
    }


    public void preaseMessage(String message){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            
            this.textContent = jsonNode.get("textContent").asText();
            this.msgType = jsonNode.get("msgType").asText();
            this.sendTime = new Date();
            this.sendTimeTS = new Date().getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
