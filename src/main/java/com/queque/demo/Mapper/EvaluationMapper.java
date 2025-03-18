package com.queque.demo.Mapper;

import com.queque.demo.Entity.Evaluation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EvaluationMapper {
    // 插入一条评价
    @Insert("INSERT INTO evaluation( userid,send_userid, score, feedback, creattime) VALUES(#{chatid}, #{userid}, #{send_userid}, #{score}, #{feedback}, #{creattime})")
    void addEvaluation( String userid,String send_userid, int score, String feedback,  String creattime);

    // 获取某位用户发过的评价
    @Select("SELECT * FROM evaluation WHERE send_userid = #{send_userid}")
    List<Evaluation> getEvaluationBySendUserId(String send_userid);

    // 获取对某位用户的评价
    @Select("SELECT * FROM evaluation WHERE userid = #{userid}")
    List<Evaluation> getEvaluationByUserId(String userid);
}
