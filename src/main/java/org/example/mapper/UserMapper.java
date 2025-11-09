package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.entity.User;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    User findByEmail(@Param("email") String email);

    /**
     * 插入新用户
     */
    int insert(User user);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("username") String username, @Param("password") String password);

    /**
     * 根据ID查询用户
     */
    User findById(@Param("id") Long id);
}

