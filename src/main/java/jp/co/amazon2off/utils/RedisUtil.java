package jp.co.amazon2off.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 根据key获取String值
     */
    public String get(Object key) {
        return getRedisTemplate().opsForValue().get(key) == null ? null : getRedisTemplate().opsForValue().get(key).toString();
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(Object key) {
        return getRedisTemplate().hasKey(key.toString());
    }

    /**
     * 将值放入缓存
     */
    @SuppressWarnings("unchecked")
    public void setString(String key, Object value) {
        getRedisTemplate().opsForValue().set(key, value.toString());
    }

    /**
     * 将值放入缓存，设置时间
     */
    @SuppressWarnings("unchecked")
    public void setString(String key, Object value, long secs) {
        if (secs > 0) {
            getRedisTemplate().opsForValue().set(key, value.toString(), secs, TimeUnit.SECONDS);
        } else {
            this.setString(key, value.toString());
        }
    }

    /**
     * 将值放入缓存，设置时间
     */
    @SuppressWarnings("unchecked")
    public void setString(String key, Object value, long num, TimeUnit timeUnit) {
        getRedisTemplate().opsForValue().set(key, value.toString(), num, timeUnit);
    }

    /**
     * 删除key
     */
    @SuppressWarnings("unchecked")
    public Boolean delete(String key) {
        return getRedisTemplate().delete(key);
    }

    /**
     * 给一个指定的 key 值附加过期时间
     */
    @SuppressWarnings("unchecked")
    public void expire(String key, long secs) {
        getRedisTemplate().expire(key, secs, TimeUnit.SECONDS);
    }

    /**
     * 对一个 key-value 的值进行加减操作,
     * 如果该 key 不存在 将创建一个key 并赋值该 number
     * 如果 key 存在,但 value 不是 纯数字 ,将报错
     */
    @SuppressWarnings("unchecked")
    public Long increment(String key, long number) {
        return getRedisTemplate().opsForValue().increment(key, number);
    }
}
