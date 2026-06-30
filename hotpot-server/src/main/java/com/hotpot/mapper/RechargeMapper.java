package com.hotpot.mapper;

import com.hotpot.entity.RechargeRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RechargeMapper {
    @Select("SELECT * FROM recharge_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<RechargeRecord> findByUser(Long userId);
    @Select("SELECT * FROM recharge_record ORDER BY create_time DESC")
    List<RechargeRecord> findAll();
    @Insert("INSERT INTO recharge_record(user_id,amount,pay_method,status,trade_no,create_time) VALUES(#{userId},#{amount},#{payMethod},#{status},#{tradeNo},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RechargeRecord record);
    @Update("UPDATE recharge_record SET status=#{status} WHERE trade_no=#{tradeNo}")
    int updateStatus(String tradeNo, Integer status);
}
