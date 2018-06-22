package com.snail.test;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class JdbcRealmTest {

    private DruidDataSource dataSource = new DruidDataSource();
    {
        dataSource.setUrl("jdbc:mysql://192.168.44.222:3306/scott");
        dataSource.setUsername("mysql");
        dataSource.setPassword("123456");
    }

    @Test
    public void testJdbcRealm(){
        JdbcRealm realm = new JdbcRealm();
        realm.setDataSource(dataSource);
        // 开启查询权限
        realm.setPermissionsLookupEnabled(true);
        realm.setAuthenticationQuery("select pswd from u_user where email = ?");
        realm.setUserRolesQuery("select name from u_role r where 1=1 and exists(select ur.rid from u_user_role ur where 1=1 and r.id = ur.rid and exists(select u.id from u_user u where u.id = ur.uid and u.email=?))");
        realm.setPermissionsQuery("select p.permissioncode from u_permission p where 1=1 and p.permissioncode is not null and exists(select up.pid from u_role_permission up where 1=1 and p.id = up.pid and exists(select r.id from u_role r where 1=1 and r.id = up.rid and r.name = ?))");

        // 创建 SecurityManager 环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        // 加载合法数据
        defaultSecurityManager.setRealm(realm);

        //认证主体
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        // 创建需要校验用户的凭证
        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan","123456");

        // 校验凭证
        subject.login(token);

        //校验结果
       System.out.println("token："+subject.isAuthenticated());

       subject.checkRole("user");

       subject.checkPermission("authority:find1");
    }
}
