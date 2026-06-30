package com.hotpot.mapper;

import com.hotpot.entity.HotpotBase;
import com.hotpot.entity.HotpotType;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HotpotBaseMapper {
    @Select("SELECT * FROM hotpot_base WHERE id = #{id}")
    HotpotBase findById(Long id);
    @Select("SELECT * FROM hotpot_base WHERE status = 1 ORDER BY sort")
    List<HotpotBase> findAll();
    @Select("SELECT * FROM hotpot_base WHERE type_id = #{typeId} AND status = 1 ORDER BY sort")
    List<HotpotBase> findByType(Long typeId);
    @Insert("INSERT INTO hotpot_base(type_id,name,image,description,price,spicy_min,spicy_max,num_min,num_max,pot_type,status,sort,create_time) VALUES(#{typeId},#{name},#{image},#{description},#{price},#{spicyMin},#{spicyMax},#{numMin},#{numMax},#{potType},#{status},#{sort},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HotpotBase base);
    @Update("UPDATE hotpot_base SET type_id=#{typeId},name=#{name},image=#{image},description=#{description},price=#{price},spicy_min=#{spicyMin},spicy_max=#{spicyMax},num_min=#{numMin},num_max=#{numMax},pot_type=#{potType},status=#{status},sort=#{sort} WHERE id=#{id}")
    int update(HotpotBase base);
    @Delete("DELETE FROM hotpot_base WHERE id = #{id}")
    int delete(Long id);
    @Select("SELECT * FROM hotpot_type WHERE status = 1 ORDER BY sort")
    List<HotpotType> findAllTypes();
    @Insert("INSERT INTO hotpot_type(name,description,image,status,sort,create_time) VALUES(#{name},#{description},#{image},#{status},#{sort},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertType(HotpotType type);
    @Update("UPDATE hotpot_type SET name=#{name},description=#{description},image=#{image},status=#{status},sort=#{sort} WHERE id=#{id}")
    int updateType(HotpotType type);
    @Delete("DELETE FROM hotpot_type WHERE id = #{id}")
    int deleteType(Long id);
}
