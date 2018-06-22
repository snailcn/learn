package com.snail.realm;


import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义 Realm
 */
public class CustomRealm extends AuthorizingRealm {

    private DataSource dataSource;

    public CustomRealm(DataSource dataSource) {
        super.setName("customRealm");
        this.dataSource = dataSource;
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
        try {
            Set<String> roles = getRolesByUserName(this.dataSource.getConnection(), userName);
            Set<String> permissions = getPermissionsByRoleName(this.dataSource.getConnection(),roles);
            // 添加角色
            simpleAuthorizationInfo.setRoles(roles);
            // 添加权限
            simpleAuthorizationInfo.setStringPermissions(permissions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        String password = null;
        try {
            password = getPasswordForName(this.dataSource.getConnection(), userName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (password == null) {
            return null;
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo("zhangsan", password, "customRealm");
        return simpleAuthenticationInfo;
    }

    private Set<String> getPermissionsByRoleName(Connection connection, Set<String> roles) {
        String coditionIn = "";
        for(String roleName : roles){
            coditionIn += "'"+roleName+"',";
        }
        if(!"".equals(coditionIn)){
            coditionIn = coditionIn.substring(0,coditionIn.length()-1);
        }
        String sql = "select p.permissioncode from u_permission p where 1=1 and p.permissioncode is not null and exists(select up.pid from u_role_permission up where 1=1 and p.id = up.pid and exists(select r.id from u_role r where 1=1 and r.id = up.rid and r.name in("+coditionIn+")))";
        Set<String> sets = new HashSet<String>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String permission = resultSet.getString(1);
                if (permission != null) {
                    sets.add(permission);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(ps);
        }
        return sets;
    }

    private Set<String> getRolesByUserName(Connection connection, String userName) {
        Set<String> sets = new HashSet<String>();
        String sql = "select name from u_role r where 1=1 and exists(select ur.rid from u_user_role ur where 1=1 and r.id = ur.rid and exists(select u.id from u_user u where u.id = ur.uid and u.email=?))";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, userName);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                String roleName = resultSet.getString(1);
                if (roleName != null) {
                    sets.add(roleName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(ps);
        }
        return sets;
    }

    private String getPasswordForName(Connection connection, String userName) {
        String password = "";
        String sql = "select pswd as password from u_user where email = ?";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, userName);
            resultSet = ps.executeQuery();
            if (resultSet.first()) {
                password = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeStatement(ps);
        }
        return password;
    }
}
