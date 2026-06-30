package com.hotpot.mapper;

import com.hotpot.entity.PointsRecord;
import com.hotpot.entity.PointsGoods;
import com.hotpot.entity.ExchangeRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PointsMapper {
    @Select("SELECT * FROM points_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<PointsRecord> findRecordsByUser(Long userId);
    @Insert("INSERT INTO points_record(user_id,points,type,description,order_id,create_time) VALUES(#{userId},#{points},#{type},#{description},#{orderId},NOW())")
    int insertRecord(PointsRecord record);
    @Select("SELECT * FROM points_goods WHERE status = 1 ORDER BY sort")
    List<PointsGoods> findGoods();
    @Select("SELECT * FROM points_goods WHERE id = #{id}")
    PointsGoods findGoodsById(Long id);
    @Insert("INSERT INTO points_goods(name,image,description,points,stock,status,sort,create_time) VALUES(#{name},#{image},#{description},#{points},#{stock},#{status},#{sort},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertGoods(PointsGoods goods);
    @Update("UPDATE points_goods SET name=#{name},image=#{image},description=#{description},points=#{points},stock=#{stock},status=#{status},sort=#{sort} WHERE id=#{id}")
    int updateGoods(PointsGoods goods);
    @Delete("DELETE FROM points_goods WHERE id = #{id}")
    int deleteGoods(Long id);
    @Update("UPDATE points_goods SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(Long id, Integer quantity);
    @Select("SELECT * FROM exchange_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ExchangeRecord> findExchangesByUser(Long userId);
    @Select("SELECT * FROM exchange_record ORDER BY create_time DESC")
    List<ExchangeRecord> findAllExchanges();
    @Insert("INSERT INTO exchange_record(user_id,goods_id,goods_name,points,quantity,status,remark,create_time) VALUES(#{userId},#{goodsId},#{goodsName},#{points},#{quantity},#{status},#{remark},NOW())")
    int insertExchange(ExchangeRecord record);
    @Update("UPDATE exchange_record SET status=#{status},remark=#{remark} WHERE id=#{id}")
    int updateExchange(Long id, Integer status, String remark);
}
