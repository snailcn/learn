package com.snail.controller;

import com.snail.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @RequestMapping(value = "/loginAuth", method = RequestMethod.POST)
    @ResponseBody
    public String login(User user, String role, String permission) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try {
            token.setRememberMe(user.isRememberMe());
            subject.login(token);
            if (subject.hasRole(role)) {
                System.out.println("具有：【" + role + "】角色");
            } else {
                System.out.println("没有：【" + role + "】角色");
            }
            if (subject.isPermitted(permission)) {
                System.out.println("具有：【" + permission + "】权限");
            } else {
                System.out.println("没有：【" + permission + "】权限");
            }
        } catch (AuthenticationException e) {
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "认证成功";
    }

    /**
     * 测试通过注解配置角色，只有具有指定的角色才能访问
     *
     * @return
     */
    @RequiresRoles(value = "sysAdmin")
    @RequestMapping(value = "/testRole", method = RequestMethod.GET)
    @ResponseBody
    public String testRole() {
        return "Have admin Role";
    }

    /**
     * 测试通过注解配置角色，只有具有指定的角色才能访问
     *
     * @return
     */
    @RequestMapping(value = "/testFilter", method = RequestMethod.GET)
    @ResponseBody
    public String testFilter() {
        return "Have admin Role";
    }
    /**
     * 测试通过注解配置角色，只有具有指定的角色才能访问
     *
     * @return
     */
    @RequestMapping(value = "/testCustomFilter", method = RequestMethod.GET)
    @ResponseBody
    public String testCustomFilter() {
        return "Have admin Role";
    }

    /**
     * 测试通过注解配置权限，只有具有指定权限才能访问
     *
     * @return
     */
    @RequiresRoles(value = "user:find")
    @RequestMapping(value = "/testPermission", method = RequestMethod.GET)
    @ResponseBody
    public String testPermission() {
        return "Have user:find rights";
    }
}
