package com.hotpot.mapper;

import com.hotpot.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> findAll();

    @Insert("INSERT INTO user(username,password,nickname,phone,avatar,gender,email,balance,points,status,create_time) " +
            "VALUES(#{username},#{password},#{nickname},#{phone},#{avatar},#{gender},#{email},#{balance},#{points},#{status},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET nickname=#{nickname},phone=#{phone},avatar=#{avatar},gender=#{gender},email=#{email},update_time=NOW() WHERE id=#{id}")
    int update(User user);

    @Update("UPDATE user SET password=#{password},update_time=NOW() WHERE id=#{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE user SET balance = balance + #{amount},update_time=NOW() WHERE id=#{id}")
    int addBalance(@Param("id") Long id, @Param("amount") Integer amount);

    @Update("UPDATE user SET points = points + #{points},update_time=NOW() WHERE id=#{id}")
    int addPoints(@Param("id") Long id, @Param("points") Integer points);

    @Update("UPDATE user SET points = points - #{points},update_time=NOW() WHERE id=#{id} AND points >= #{points}")
    int deductPoints(@Param("id") Long id, @Param("points") Integer points);
}
