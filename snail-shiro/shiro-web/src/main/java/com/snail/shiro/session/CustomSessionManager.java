package com.snail.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * 自定义 SessionManager 主要实现 减少对 redis 的访问次数 减轻 redis 的压力
 */
public class CustomSessionManager extends DefaultWebSessionManager {
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        Session session = null;
        // 只有 sessionKey 为 WebSessionKey 时才能获取 ServletRequest
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        // 当 ServletRequest 不为空，请求 sessionId 不为空时直接从 ServletRequest 中获取
        if (request != null && sessionId != null) {
            session = (Session) request.getAttribute(sessionId.toString());
            // 获取到的 session 不为空才返回
            if (session != null) {
                return session;
            }
        }
        // 从 redis 中获取
        session = super.retrieveSession(sessionKey);
        if (request != null && sessionId != null) {
            // 将session 设置到 ServletRequest 中
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }
}
