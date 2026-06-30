package com.hotpot.mapper;

import com.hotpot.entity.UserFavorite;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserFavoriteMapper {
    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserFavorite> findByUser(Long userId);
    @Select("SELECT * FROM user_favorite WHERE user_id = #{userId} AND dish_id = #{dishId}")
    UserFavorite findByUserAndDish(Long userId, Long dishId);
    @Insert("INSERT INTO user_favorite(user_id,dish_id,create_time) VALUES(#{userId},#{dishId},NOW())")
    int insert(UserFavorite favorite);
    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND dish_id = #{dishId}")
    int delete(Long userId, Long dishId);
    @Select("SELECT COUNT(*) FROM user_favorite WHERE dish_id = #{dishId}")
    int countByDish(Long dishId);
}
