package com.hotpot.mapper;

import com.hotpot.entity.Role;
import com.hotpot.entity.Menu;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RoleMapper {
    @Select("SELECT r.* FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId}")
    List<Role> findByUserId(Long userId);
    @Select("SELECT m.* FROM menu m INNER JOIN role_menu rm ON m.id = rm.menu_id WHERE rm.role_id = #{roleId} AND m.status = 1 ORDER BY m.sort")
    List<Menu> findMenusByRoleId(Long roleId);
    @Select("SELECT * FROM role ORDER BY id")
    List<Role> findAll();
    @Select("SELECT * FROM menu ORDER BY sort")
    List<Menu> findAllMenus();
    @Insert("INSERT INTO role(name,code,description,status,create_time) VALUES(#{name},#{code},#{description},#{status},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);
    @Update("UPDATE role SET name=#{name},code=#{code},description=#{description},status=#{status} WHERE id=#{id}")
    int update(Role role);
    @Delete("DELETE FROM role WHERE id = #{id}")
    int delete(Long id);
    @Insert("INSERT INTO menu(parent_id,name,path,component,icon,type,permission,sort,status,create_time) VALUES(#{parentId},#{name},#{path},#{component},#{icon},#{type},#{permission},#{sort},#{status},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMenu(Menu menu);
    @Update("UPDATE menu SET parent_id=#{parentId},name=#{name},path=#{path},component=#{component},icon=#{icon},type=#{type},permission=#{permission},sort=#{sort},status=#{status} WHERE id=#{id}")
    int updateMenu(Menu menu);
    @Delete("DELETE FROM menu WHERE id = #{id}")
    int deleteMenu(Long id);
}
