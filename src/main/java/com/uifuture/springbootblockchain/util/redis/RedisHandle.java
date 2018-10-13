package com.uifuture.springbootblockchain.util.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 增删改 -不能在这里面抓取异常 -因为可能有事务处理
 * @author chenhx
 * @version RedisHandle.java, v 0.1 2018-10-11 下午 9:18
 */
@Component
public class RedisHandle implements RedisCommand<String, Object> {
    /**
     * 默认缓存时间为365天
     */
    private static final Long TIME_CACHE = 60 * 60 * 24 * 365L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHandle.class);
    @Autowired
    protected RedisTemplate redisTemplate;

    @Override
    public double getCreateTimeScore(long date) {
        return date / 100000.0;
    }


    @Override
    public void addList(String key, List<Object> objectList) {
        for (Object obj : objectList) {
            addList(key, obj);
        }
    }

    @Override
    public long addList(String key, Object obj) {
        return redisTemplate.boundListOps(key).rightPush(obj);
    }

    @Override
    public long addList(String key, Object... obj) {
        return redisTemplate.boundListOps(key).rightPushAll(obj);
    }

    @Override
    public List<Object> getList(String key, long s, long e) {
        return redisTemplate.boundListOps(key).range(s, e);
    }

    @Override
    public List<Object> getList(String key) {
        return redisTemplate.boundListOps(key).range(0, getListSize(key));
    }

    @Override
    public long getListSize(String key) {
        return redisTemplate.boundListOps(key).size();
    }

    @Override
    public long removeListValue(String key, Object object) {
        return redisTemplate.boundListOps(key).remove(0, object);
    }

    @Override
    public long removeListValue(String key, Object... objects) {
        long r = 0;
        for (Object object : objects) {
            r += removeListValue(key, object);
        }
        return r;
    }

    @Override
    public void remove(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                remove(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    @Override
    public void removeBlear(String... blears) {
        for (String blear : blears) {
            removeBlear(blear);
        }
    }

    @Override
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    @Override
    public void removeMapFieldByRegular(String key, String blear) {
        Map<String, Object> map = getMap(key);
        Set<String> stringSet = map.keySet();
        for (String s : stringSet) {
            if (Pattern.compile(blear).matcher(s).matches()) {
                redisTemplate.boundHashOps(key).delete(s);
            }
        }
    }

    @Override
    public Long removeZSetValue(String key, Object... value) {
        return redisTemplate.boundZSetOps(key).remove(value);
    }

    @Override
    public void removeSet(String key) {
        //通过设置过期时间移除set
        redisTemplate.boundSetOps(key).expire(0, TimeUnit.SECONDS);
    }

    @Override
    public void removeZSet(String key) {
        removeZSetRange(key, 0L, getZSetSize(key));
    }

    @Override
    public void removeZSetRange(String key, Long start, Long end) {
        redisTemplate.boundZSetOps(key).removeRange(start, end);
    }

    @Override
    public void setZSetUnionAndStore(String key, String key1, String key2) {
        redisTemplate.boundZSetOps(key).unionAndStore(key1, key2);
    }

    @Override
    public Set<Object> getZSetRange(String key) {
        return getZSetRange(key, 0, getZSetSize(key));
    }

    @Override
    public Set<Object> getZSetRange(String key, long s, long e) {
        return redisTemplate.boundZSetOps(key).range(s, e);
    }

    @Override
    public Set<Object> getZSetReverseRange(String key) {
        return getZSetReverseRange(key, 0, getZSetSize(key));
    }

    @Override
    public Set<Object> getZSetReverseRange(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRange(start, end);
    }

    @Override
    public Set<Object> getZSetRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).rangeByScore(start, end);
    }

    @Override
    public Set<Object> getZSetReverseRangeByScore(String key, double start, double end) {
        return redisTemplate.boundZSetOps(key).reverseRangeByScore(start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).rangeWithScores(start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.boundZSetOps(key).reverseRangeWithScores(start, end);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getZSetRangeWithScores(String key) {
        return getZSetRangeWithScores(key, 0, getZSetSize(key));
    }

    @Override
    public Set<ZSetOperations.TypedTuple<Object>> getZSetReverseRangeWithScores(String key) {
        return getZSetReverseRangeWithScores(key, 0, getZSetSize(key));
    }

    @Override
    public long getZSetCountSize(String key, double sMin, double sMax) {
        return redisTemplate.boundZSetOps(key).count(sMin, sMax);
    }

    @Override
    public long getZSetSize(String key) {
        return redisTemplate.boundZSetOps(key).size();
    }

    @Override
    public double getZSetScore(String key, Object value) {
        return redisTemplate.boundZSetOps(key).score(value);
    }

    @Override
    public double incrementZSetScore(String key, Object value, double delta) {
        return redisTemplate.boundZSetOps(key).incrementScore(value, delta);
    }

    @Override
    public Boolean addZSet(String key, double score, Object value) {
        return redisTemplate.boundZSetOps(key).add(value, score);
    }

    @Override
    public Long addZSet(String key, TreeSet<Object> value) {
        return redisTemplate.boundZSetOps(key).add(value);
    }

    @Override
    public Boolean addZSet(String key, double[] score, Object[] value) {
        if (score.length != value.length) {
            return false;
        }
        for (int i = 0; i < score.length; i++) {
            if (addZSet(key, score[i], value[i]) == false) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    @Override
    public void removeZSetRangeByScore(String key, double s, double e) {
        redisTemplate.boundZSetOps(key).removeRangeByScore(s, e);
    }

    @Override
    public Boolean setSetExpireTime(String key, Long time) {
        return redisTemplate.boundSetOps(key).expire(time, TimeUnit.SECONDS);
    }

    @Override
    public Boolean setZSetExpireTime(String key, Long time) {
        return redisTemplate.boundZSetOps(key).expire(time, TimeUnit.SECONDS);
    }

    @Override
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.boundValueOps(key).get();
    }

    @Override
    public List<Object> get(String... keys) {
        List<Object> list = new ArrayList<Object>();
        for (String key : keys) {
            list.add(get(key));
        }
        return list;
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, TIME_CACHE);
    }

    @Override
    public void set(String key, Object value, Long expireTime) {
        redisTemplate.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public Boolean setExpireTime(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }


    @Override
    public DataType getType(String key) {
        return redisTemplate.type(key);
    }


    @Override
    public void removeMapField(String key, Object... field) {
        redisTemplate.boundHashOps(key).delete(field);
    }

    @Override
    public Long getMapSize(String key) {
        return redisTemplate.boundHashOps(key).size();
    }

    @Override
    public Map<String, Object> getMap(String key) {
        return redisTemplate.boundHashOps(key).entries();
    }

    @Override
    public <T> T getMapField(String key, String field) {
        return (T) redisTemplate.boundHashOps(key).get(field);
    }

    @Override
    public Boolean hasMapKey(String key, String field) {
        return redisTemplate.boundHashOps(key).hasKey(field);
    }

    @Override
    public List<Object> getMapFieldValue(String key) {
        return redisTemplate.boundHashOps(key).values();
    }

    @Override
    public Set<Object> getMapFieldKey(String key) {
        return redisTemplate.boundHashOps(key).keys();
    }

    @Override
    public void addMap(String key, Map<String, Object> map) {
        redisTemplate.boundHashOps(key).putAll(map);
    }

    @Override
    public void addMap(String key, String field, Object value) {
        redisTemplate.boundHashOps(key).put(field, value);
    }

    @Override
    public void addMap(String key, String field, Object value, long time) {
        redisTemplate.boundHashOps(key).put(field, value);
        redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }

    @Override
    public void watch(String key) {
        redisTemplate.watch(key);
    }

    @Override
    public void addSet(String key, Object... obj) {
        redisTemplate.boundSetOps(key).add(obj);
    }

    @Override
    public long removeSetValue(String key, Object obj) {
        return redisTemplate.boundSetOps(key).remove(obj);
    }

    @Override
    public long removeSetValue(String key, Object... obj) {
        if (obj != null && obj.length > 0) {
            return redisTemplate.boundSetOps(key).remove(obj);
        }
        return 0L;
    }

    @Override
    public long getSetSize(String key) {
        return redisTemplate.boundSetOps(key).size();
    }

    @Override
    public Boolean hasSetValue(String key, Object obj) {
        return redisTemplate.boundSetOps(key).isMember(obj);
    }

    @Override
    public Set<Object> getSet(String key) {
        return redisTemplate.boundSetOps(key).members();
    }

    @Override
    public Set<Object> getSetUnion(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).union(otherKey);
    }

    @Override
    public Set<Object> getSetUnion(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).union(set);
    }

    @Override
    public Set<Object> getSetIntersect(String key, String otherKey) {
        return redisTemplate.boundSetOps(key).intersect(otherKey);
    }

    @Override
    public Set<Object> getSetIntersect(String key, Set<Object> set) {
        return redisTemplate.boundSetOps(key).intersect(set);
    }

    @Override
    public Boolean setMapExpireTime(String key, Long time) {
        return redisTemplate.boundHashOps(key).expire(time, TimeUnit.SECONDS);
    }

    @Override
    public boolean setMapAndExpireTimeByRedis(String key, String fk, Object value, Long time) {
        addMap(key, fk, value);
        return setMapExpireTime(key, time);
    }
}
