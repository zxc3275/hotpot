package com.hotpot.mapper;

import com.hotpot.entity.CartItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CartMapper {
    @Select("SELECT * FROM cart_item WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<CartItem> findByUserId(Long userId);
    @Select("SELECT * FROM cart_item WHERE user_id = #{userId} AND dish_id = #{dishId}")
    CartItem findByUserAndDish(@Param("userId") Long userId, @Param("dishId") Long dishId);
    @Insert("INSERT INTO cart_item(user_id,dish_id,dish_name,dish_image,dish_price,quantity,create_time) VALUES(#{userId},#{dishId},#{dishName},#{dishImage},#{dishPrice},#{quantity},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem item);
    @Update("UPDATE cart_item SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    @Delete("DELETE FROM cart_item WHERE id = #{id}")
    int delete(Long id);
    @Delete("DELETE FROM cart_item WHERE user_id = #{userId}")
    int clearByUser(Long userId);
    @Delete("DELETE FROM cart_item WHERE user_id = #{userId} AND id IN (${ids})")
    int deleteByIds(@Param("userId") Long userId, @Param("ids") String ids);
}
