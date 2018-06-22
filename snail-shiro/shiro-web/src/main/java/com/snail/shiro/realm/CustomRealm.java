package com.snail.shiro.realm;

import com.snail.dao.UserDao;
import com.snail.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义 Realm ，认证信息从自定义的数据库表中获取
 */
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserDao userDao;

    public CustomRealm() {
        super.setName("customRealm");
    }

    /**
     * 校验角色与权限
     *
     * @param principalCollection
     * @return
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userName = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        Set<String> roles = getRolesByUserName(userName);
        Set<String> permissions = getPermissionsByRoleName(roles);
        // 添加角色
        simpleAuthorizationInfo.setRoles(roles);
        // 添加权限
        simpleAuthorizationInfo.setStringPermissions(permissions);

        return simpleAuthorizationInfo;
    }

    /**
     * 校验用户凭证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 获取用户名
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String userName = token.getUsername();

        // 根据用户名到数据库中获取密码
        User user = getPasswordForName(userName);

        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), "customRealm");
        return simpleAuthenticationInfo;
    }

    private Set<String> getPermissionsByRoleName(Set<String> roles) {
        List<String> list = userDao.getPermissionsByRoleName(new ArrayList<String>(roles));
        return new HashSet<String>(list);
    }

    private Set<String> getRolesByUserName(String userName) {
        List<String> list = userDao.getRolesByUserName(userName);
        return new HashSet<String>(list);
    }

    private User getPasswordForName(String userName) {
        return userDao.findByName(userName);
    }
}