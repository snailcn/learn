package com.snail.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.snail.realm.CustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class CustomRealmTest {

    private DruidDataSource dataSource = new DruidDataSource();
    {
        dataSource.setUrl("jdbc:mysql://192.168.44.222:3306/scott");
        dataSource.setUsername("mysql");
        dataSource.setPassword("123456");
    }

    @Test
    public void testCustomRealm() {

        CustomRealm realm = new CustomRealm(dataSource);

        // 创建 SecurityManager 环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        // 加载合法用户信息
        defaultSecurityManager.setRealm(realm);

        // 认证主体
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        // 需要认证的凭证
        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan", "123456");

        // 对凭证进行认证
        subject.login(token);

        // 获取认证结果
        if (subject.isAuthenticated()) {
            System.out.println("认证通过");
        } else {
            System.out.println("认证失败");
        }

        // 验证角色
        subject.checkRole("user");
        // 验证权限
        subject.checkPermission("user:delete");

    }
}
