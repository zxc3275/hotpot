package com.hotpot.mapper;

import com.hotpot.entity.CookingGuide;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface GuideMapper {
    @Select("SELECT * FROM cooking_guide WHERE type = #{type} AND status = 1 ORDER BY sort")
    List<CookingGuide> findByType(String type);
    @Select("SELECT * FROM cooking_guide ORDER BY type, sort")
    List<CookingGuide> findAll();
    @Select("SELECT * FROM cooking_guide WHERE id = #{id}")
    CookingGuide findById(Long id);
    @Insert("INSERT INTO cooking_guide(title,content,image,type,status,sort,create_time) VALUES(#{title},#{content},#{image},#{type},#{status},#{sort},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CookingGuide guide);
    @Update("UPDATE cooking_guide SET title=#{title},content=#{content},image=#{image},type=#{type},status=#{status},sort=#{sort} WHERE id=#{id}")
    int update(CookingGuide guide);
    @Delete("DELETE FROM cooking_guide WHERE id = #{id}")
    int delete(Long id);
}
