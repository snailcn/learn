package com.snail.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

public class AuthenticationTest {


    SimpleAccountRealm realm = new SimpleAccountRealm();

    @Before
    public void addUser(){
        realm.addAccount("zhangsan","123456","admin");
    }

    @Test
    public void testAuthentication(){
        // 构建 SecurityManager 环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        // 添加合法用户
        defaultSecurityManager.setRealm(realm);

        // 认证主体
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        // 凭证对象
        UsernamePasswordToken token = new UsernamePasswordToken("zhangsan","123456");

        subject.login(token);

        System.out.println(subject.isAuthenticated());

        subject.checkRole("admin");
    }
}
