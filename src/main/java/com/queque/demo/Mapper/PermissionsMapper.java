package com.queque.demo.Mapper;

import com.queque.demo.Entity.PermissionName;
import com.queque.demo.Entity.Permissions;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PermissionsMapper {
    @Insert("INSERT INTO permissions(name, description, permissions) VALUES(#{name}, #{description}, #{permissions})")
    void addPermissions(String name, String description, String permissions);

    @Update("UPDATE permissions SET description = #{description}, permissions = #{permissions} WHERE name = #{name}")
    void updatePermissions(String name, String description, String permissions);

    @Delete("DELETE FROM permissions WHERE name = #{name}")
    void deletePermissions(String name);

    @Select("SELECT * FROM permissions")
    List<Permissions> getPermissions();

    //获取全部权限的名称
    @Select("SELECT * FROM permission_name")
    List<PermissionName> getPermissionName();
}
