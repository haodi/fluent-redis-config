package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class HelloRedisService {

    @Autowired
    @Qualifier("hello-redis")
    private JedisPool jedisPool;

    public void hello() {
        try (Jedis jedis = jedisPool.getResource()) {
            System.out.println(jedis.info());
        }
    }
}
