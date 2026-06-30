package com.hotpot.mapper;

import com.hotpot.entity.DishCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DishCategoryMapper {
    @Select("SELECT * FROM dish_category ORDER BY sort")
    List<DishCategory> findAll();

    @Select("SELECT * FROM dish_category WHERE id = #{id}")
    DishCategory findById(Long id);

    @Insert("INSERT INTO dish_category(name,icon,sort,status,create_time) VALUES(#{name},#{icon},#{sort},#{status},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DishCategory category);

    @Update("UPDATE dish_category SET name=#{name},icon=#{icon},sort=#{sort},status=#{status} WHERE id=#{id}")
    int update(DishCategory category);

    @Delete("DELETE FROM dish_category WHERE id = #{id}")
    int delete(Long id);
}
