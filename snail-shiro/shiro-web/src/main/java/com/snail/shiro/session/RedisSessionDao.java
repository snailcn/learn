package com.snail.shiro.session;

import com.snail.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 自定义 Shiro 的 session管理dao，通过Redis管理session共享
 */
public class RedisSessionDao extends AbstractSessionDAO {

    /**
     * session 前缀
     */
    private final String SHIRO_SESSION_KEY_PREFIX = "snail-session:";

    @Resource
    private JedisUtil jedisUtil;

    /**
     * 获取二进制的 key
     * @param key
     * @return
     */
    private byte[] getKey(String key){
        return (this.SHIRO_SESSION_KEY_PREFIX + key).getBytes();
    }

    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session,sessionId);
        saveSession(session);
        return sessionId;
    }

    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        byte[] key = getKey(sessionId.toString());
        byte[] value = jedisUtil.get(key);
        return (Session)SerializationUtils.deserialize(value);
    }

    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    private void saveSession(Session session) {
        if (session != null && session.getId() != null){
            byte[] key = this.getKey(session.getId().toString());
            byte[] value = SerializationUtils.serialize(session);
            jedisUtil.set(key,value);
            jedisUtil.expire(key,600);
        }
    }

    public void delete(Session session) {
        if (session != null && session.getId() != null){
            byte[] key = getKey(session.getId().toString());
            jedisUtil.remove(key);
        }
    }

    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.getKeys(this.SHIRO_SESSION_KEY_PREFIX);
        Set<Session> sessions = new HashSet<Session>();
        if(!CollectionUtils.isEmpty(keys)){
            for (byte[] key : keys) {
                Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
                sessions.add(session);
            }
        }
        return sessions;
    }
}
