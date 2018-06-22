package com.snail.shiro.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义 Shiro 的授权过滤器，只要具备其中一个角色即可访问
 */
public class RolesOrFilter extends AuthorizationFilter {
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String[] array = (String[]) o;
        if (array == null && array.length == 0) {
            return true;
        }
        for (String s : array) {
            if (subject.hasRole(s)) {
                return true;
            }
        }
        return false;
    }
}
