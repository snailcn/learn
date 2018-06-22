package com.snail.shiro.cache;

import com.snail.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.util.*;

@Component
public class RedisCache<K, V> implements Cache<K, V> {

    /**
     * 缓存key的前缀
     */
    private final String CACHE_KEY_PREFIX = "snail-cache:";

    @Resource
    private JedisUtil jedisUtil;

    /**
     * 将 key 转换为 二进制 存储
     *
     * @param k
     * @return
     */
    private byte[] getKey(K k) {
        if (k instanceof String) {
            return (this.CACHE_KEY_PREFIX + k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    public V get(K k) throws CacheException {
        byte[] value = jedisUtil.get(this.getKey(k));
        if (value != null) {
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    public V put(K k, V v) throws CacheException {
        byte[] key = this.getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key, value);
        jedisUtil.expire(key, 600);
        return v;
    }

    public V remove(K k) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.remove(key);
        if (value != null) {
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    public void clear() throws CacheException {
        for (K k : this.keys()) {
            this.remove(k);
        }
    }

    public int size() {
        return this.keys().size();
    }

    public Set<K> keys() {
        Set<byte[]> keys = jedisUtil.getKeys(this.CACHE_KEY_PREFIX);
        Set<K> sets = new HashSet<K>();
        for (byte[] key : keys) {
            sets.add((K) SerializationUtils.deserialize(key));
        }
        return sets;
    }

    public Collection<V> values() {
        List<V> values = new ArrayList<V>();
        for (K k : this.keys()) {
            byte[] bytes = jedisUtil.get(SerializationUtils.serialize(k));
            values.add((V) SerializationUtils.deserialize(bytes));
        }
        return values;
    }
}
