package com.hotpot.mapper;

import com.hotpot.entity.Dish;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DishMapper {
    @Select("SELECT * FROM dish WHERE id = #{id}")
    Dish findById(Long id);

    @Select("SELECT * FROM dish WHERE status = 1 ORDER BY sort")
    List<Dish> findAll();

    @Select("SELECT * FROM dish WHERE category_id = #{categoryId} AND status = 1 ORDER BY sort")
    List<Dish> findByCategory(Long categoryId);

    @Select("SELECT * FROM dish WHERE status = 1 AND name LIKE CONCAT('%',#{keyword},'%') ORDER BY sort")
    List<Dish> search(String keyword);

    @Insert("INSERT INTO dish(category_id,name,image,description,price,original_price,spicy_level,status,sales,sort,create_time) " +
            "VALUES(#{categoryId},#{name},#{image},#{description},#{price},#{originalPrice},#{spicyLevel},#{status},#{sales},#{sort},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Dish dish);

    @Update("UPDATE dish SET category_id=#{categoryId},name=#{name},image=#{image},description=#{description}," +
            "price=#{price},original_price=#{originalPrice},spicy_level=#{spicyLevel},status=#{status},sort=#{sort},update_time=NOW() WHERE id=#{id}")
    int update(Dish dish);

    @Delete("DELETE FROM dish WHERE id = #{id}")
    int delete(Long id);

    @Update("UPDATE dish SET sales = sales + #{quantity} WHERE id = #{id}")
    int addSales(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Select("SELECT * FROM dish WHERE status = 1 ORDER BY sales DESC LIMIT #{limit}")
    List<Dish> findTopSales(@Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM dish")
    long count();
}
