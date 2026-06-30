package com.hotpot.mapper;

import com.hotpot.entity.Feedback;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FeedbackMapper {
    @Select("SELECT * FROM feedback WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Feedback> findByUser(Long userId);
    @Select("SELECT * FROM feedback ORDER BY create_time DESC")
    List<Feedback> findAll();
    @Insert("INSERT INTO feedback(user_id,content,status,create_time) VALUES(#{userId},#{content},0,NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Feedback feedback);
    @Update("UPDATE feedback SET reply=#{reply},status=1,reply_time=NOW() WHERE id=#{id}")
    int reply(Long id, String reply);
}
