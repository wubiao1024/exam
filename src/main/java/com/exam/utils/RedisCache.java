package com.exam.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {

    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存的基本对象。Integer String 实体类
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @param <T>
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setCacheObject(String key, T value) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(key, value);
        return operations;
    }

    /**
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @param <T>
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(key, value, timeout, timeUnit);
        return operations;
    }

    /**
     * 获得缓存的基本对象
     *
     * @param key 缓存键值
     * @param <T>
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(String key) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public void deleteObject(String key) {
        redisTemplate.delete(key);
    }


    /**
     * 删除集合对象
     *
     * @param collection
     */
    public void deleteObject(Collection collection) {
        redisTemplate.delete(collection);
    }


    /**
     * 缓存list数据
     *
     * @param key      缓存的键值
     * @param dataList 带缓存的list数据
     * @param <T>
     * @return 缓存的对象
     */
    public <T> ListOperations<String, T> setCacheList(String key, List<T> dataList) {
        ListOperations<String, T> listOperations = redisTemplate.opsForList();
        if (dataList != null) {
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                listOperations.leftPush(key, dataList.get(i));
            }
        }
        return listOperations;
    }


    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @param <T>
     * @return 缓存键值对应的集合数据
     */
    public <T> List<T> getCacheList(String key) {
        List<T> list = new ArrayList<>();
        ListOperations<String, T> listOperations = redisTemplate.opsForList();
        Long size = listOperations.size(key);

        for (int i = 0; i < size; i++) {
            list.add(listOperations.index(key, i));
        }
        return list;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key key
     * @return 缓存的set
     */
    public <T> Set<T> getCacheSet(String key) {
        Set<T> dataSet = new HashSet<>();
        BoundSetOperations<String, T> operation = redisTemplate.boundSetOps(key);
        dataSet = operation.members();
        return dataSet;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, String, T> setCacheMap(String key, Map<String, T> dataMap) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) {
            for (Map.Entry<String, T> entry : dataMap.entrySet()) {
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return hashOperations;
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(String key) {
        Map<String, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return
     */
    public Collection<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public Long getUID() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //String.format("%1$02d", var)
        // %后的1指第一个参数，当前只有var一个可变参数，所以就是指var。
        // $后的0表示，位数不够用0补齐，如果没有这个0（如%1$nd）就以空格补齐，0后面的n表示总长度，总长度可以可以是大于9例如（%1$010d），d表示将var按十进制转字符串，长度不够的话用0或空格补齐。
        String monthFormat = String.format("%1$02d", month + 1);
        String dayFormat = String.format("%1$02d", day);
        String hourFormat = String.format("%1$02d", hour);


        String prefix = monthFormat + dayFormat + hourFormat;


        String orderId;
        try {
            Long increment = redisTemplate.opsForValue().increment(prefix, 1);
            //往前补6位
            orderId = prefix + String.format("%1$06d", increment);
        } catch (Exception e) {
            System.out.println("生成失败");
            e.printStackTrace();
            // 生成失败
            return null;
        }
        return Long.valueOf(orderId);
    }

}
