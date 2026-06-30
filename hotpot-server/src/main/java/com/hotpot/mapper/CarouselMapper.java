package com.hotpot.mapper;

import com.hotpot.entity.Carousel;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CarouselMapper {
    @Select("SELECT * FROM carousel WHERE status = 1 AND type = #{type} ORDER BY sort")
    List<Carousel> findByType(Integer type);
    @Select("SELECT * FROM carousel ORDER BY sort")
    List<Carousel> findAll();
    @Insert("INSERT INTO carousel(title,image_url,link_url,type,sort,status,create_time) VALUES(#{title},#{imageUrl},#{linkUrl},#{type},#{sort},#{status},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Carousel carousel);
    @Update("UPDATE carousel SET title=#{title},image_url=#{imageUrl},link_url=#{linkUrl},type=#{type},sort=#{sort},status=#{status} WHERE id=#{id}")
    int update(Carousel carousel);
    @Delete("DELETE FROM carousel WHERE id = #{id}")
    int delete(Long id);
}
