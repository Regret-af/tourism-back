package com.af.tourism.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 写入Redis，不设置过期时间
     * @param key Redis键
     * @param value 写入的对象
     */
    public void set(String key, Object value) {
        writeValue(key, value, null, null);
    }

    /**
     * 写入Redis，设置过期时间
     * @param key Redis键
     * @param value 写入的对象
     * @param ttl 过期时间
     */
    public void set(String key, Object value, Duration ttl) {
        if (ttl == null) {
            set(key, value);
            return;
        }
        writeValue(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 写入Redis，设置过期时间
     * @param key Redis键
     * @param value 写入的对象
     * @param timeout 过期时间
     * @param unit 过期时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        writeValue(key, value, timeout, unit);
    }

    /**
     * 从 Redis 读取数据并反序列化为指定类型
     * @param key Redis 键
     * @param type 目标对象类型
     * @return 反序列化后的对象，若 key 不存在则返回 null
     * @param <T> 返回对象类型
     */
    public <T> T get(String key, Class<T> type) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException ex) {
            throw new IllegalStateException("未能反序列化 redis 值, key=" + key, ex);
        }
    }

    /**
     * 从 Redis 读取 JSON 字符串并反序列化为指定泛型类型
     * @param key Redis 键
     * @param typeReference 泛型类型引用
     * @return 反序列化后的对象，若 key 不存在则返回 null
     * @param <T> 返回对象类型
     */
    public <T> T get(String key, TypeReference<T> typeReference) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException ex) {
            throw new IllegalStateException("未能反序列化 redis 值, key=" + key, ex);
        }
    }

    /**
     * 删除 Redis 中指定 key
     * @param key Redis 键
     * @return 删除结果
     */
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除 Redis 中的指定 key
     * @param keys Redis 键集合
     * @return 实际删除的 key 数量
     */
    public Long delete(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return 0L;
        }
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 根据 pattern 批量删除 Redis key
     * @param pattern Redis pattern
     * @return 实际删除的 key 数量
     */
    public Long deleteByPattern(String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) {
            return 0L;
        }
        return stringRedisTemplate.delete(keys);
    }

    public Set<String> keys(String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }
        return keys;
    }

    /**
     * 对 Redis 中的数值进行自增操作
     * @param key Redis 键
     * @param delta 自增步长
     * @return 自增后的值
     */
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 设置 Redis key 的过期时间
     * @param key Redis 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 设置成功返回 true，否则返回 false
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置 Redis key 的过期时间
     * @param key Redis 键
     * @param ttl 过期时长
     * @return 设置成功返回 true，否则返回 false
     */
    public Boolean expire(String key, Duration ttl) {
        if (ttl == null) {
            return Boolean.FALSE;
        }
        return stringRedisTemplate.expire(key, ttl);
    }

    /**
     * 当 Redis key 不存在时写入值，并设置过期时间
     * @param key Redis 键
     * @param value 要写入的对象
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 写入成功返回 true；如果 key 已存在则返回 false
     */
    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        String json = toJson(key, value);
        return stringRedisTemplate.opsForValue().setIfAbsent(key, json, timeout, unit);
    }

    /**
     * 当 Redis key 不存在时写入值，并设置过期时间
     * @param key Redis 键
     * @param value 要写入的对象
     * @param ttl 过期时长
     * @return 写入成功返回 true；如果 key 已存在则返回 false
     */
    public Boolean setIfAbsent(String key, Object value, Duration ttl) {
        if (ttl == null) {
            return Boolean.FALSE;
        }
        String json = toJson(key, value);
        return stringRedisTemplate.opsForValue().setIfAbsent(key, json, ttl);
    }

    /**
     * 将 Hash 表数据存入缓存
     * @param key 缓存 key
     * @param hashKey Hash key
     * @param value Hash value
     */
    public void putHash(String key, String hashKey, Object value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, String.valueOf(value));
    }

    /**
     * 将 Hash 表数据存入缓存
     * @param key 缓存 key
     * @param values Hash 表
     */
    public void putAllHash(String key, Map<String, ?> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        Map<String, String> stringValues = new LinkedHashMap<>(values.size());
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            stringValues.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        stringRedisTemplate.opsForHash().putAll(key, stringValues);
    }

    /**
     * 获取 Hahs 类型缓存
     * @param key 缓存 key
     * @return
     */
    public Map<Object, Object> entries(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * Hash表字段增加指定步长
     * @param key 缓存 key
     * @param hashKey Hash key
     * @param delta 步长
     * @return 值
     */
    public Long incrementHash(String key, String hashKey, long delta) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * 写入Redis
     * @param key Redis键
     * @param value 要写入的对象
     * @param timeout 过期时间(允许为空)
     * @param unit 过期时间单位
     */
    private void writeValue(String key, Object value, Long timeout, TimeUnit unit) {
        String json = toJson(key, value);
        if (timeout == null || unit == null) {
            stringRedisTemplate.opsForValue().set(key, json);
            return;
        }
        stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
    }

    /**
     * 将对象序列化为 JSON 字符串
     * @param key Redis键
     * @param value 要序列化的值
     * @return JSON字符串
     */
    private String toJson(String key, Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            log.error("未能序列化 redis 值, key={}", key, ex);
            throw new IllegalStateException("未能序列化 redis 值, key=" + key, ex);
        }
    }
}
