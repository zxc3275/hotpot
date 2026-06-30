package com.hotpot.mapper;

import com.hotpot.entity.Dict;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DictMapper {
    @Select("SELECT * FROM dict WHERE dict_type = #{dictType} ORDER BY sort")
    List<Dict> findByType(String dictType);
    @Select("SELECT * FROM dict ORDER BY dict_type, sort")
    List<Dict> findAll();
    @Insert("INSERT INTO dict(dict_type,dict_key,dict_value,sort,status,remark,create_time) VALUES(#{dictType},#{dictKey},#{dictValue},#{sort},#{status},#{remark},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Dict dict);
    @Update("UPDATE dict SET dict_type=#{dictType},dict_key=#{dictKey},dict_value=#{dictValue},sort=#{sort},status=#{status},remark=#{remark} WHERE id=#{id}")
    int update(Dict dict);
    @Delete("DELETE FROM dict WHERE id = #{id}")
    int delete(Long id);
}
