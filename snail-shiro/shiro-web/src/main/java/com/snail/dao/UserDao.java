package com.snail.dao;

import com.snail.entity.User;

import java.util.List;

public interface UserDao {
    /**
     * 根据用户名查询用户对象
     *
     * @param userName
     * @return
     */
    public User findByName(String userName);

    /**
     * 根据用户名查询用户所具有的角色
     *
     * @param userName
     * @return
     */
    public List<String> getRolesByUserName(String userName);

    /**
     * 根据角色获取角色权限
     *
     * @param roles
     * @return
     */
    public List<String> getPermissionsByRoleName(List<String> roles);
}
