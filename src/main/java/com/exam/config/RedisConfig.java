package com.exam.config;

import com.exam.utils.FastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);

        // 使用StringRedisSerializer 序列化和反序列化redis 的key
        template.setKeySerializer(new StringRedisSerializer());
        // 使用serializer序列化和反序列化redis的value
        template.setValueSerializer(serializer);
        // hash 的key
        template.setHashKeySerializer(new StringRedisSerializer());
        // hash 的value
        template.setHashValueSerializer(serializer);
        return template;
    }
}

