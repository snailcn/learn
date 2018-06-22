package com.snail.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

/**
 * redis的访问工具包
 */
@Component
public class JedisUtil {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 获得 redis 连接
     *
     * @return
     */
    private Jedis getConnection() {
        return jedisPool.getResource();
    }

    /**
     * 设置 session
     *
     * @param key
     * @param value
     * @return
     */
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = this.getConnection();
        try {
            jedis.set(key, value);
            return value;
        } finally {
            jedis.close();
        }
    }

    /**
     * 设置 key 超时时间
     *
     * @param key
     * @param i
     */
    public void expire(byte[] key, int i) {
        Jedis jedis = this.getConnection();
        try {
            jedis.expire(key, i);
        } finally {
            jedis.close();
        }
    }

    /**
     * 获得 session
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        Jedis jedis = this.getConnection();
        try {
           return jedis.get(key);
        } finally {
            jedis.close();
        }
    }

    /**
     * 删除 session
     * @param key
     */
    public void remove(byte[] key) {
        Jedis jedis = this.getConnection();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    public Set<byte[]> getKeys(String keyPrefix) {
        Jedis jedis = this.getConnection();
        try {
            return jedis.keys((keyPrefix+"*").getBytes());
        } finally {
            jedis.close();
        }
    }

    /**
     *
     * @return
     */
    public int getSize(){
        Long size = this.getConnection().dbSize();
        return size.intValue();
    }
}
