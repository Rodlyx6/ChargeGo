package com.example.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class MysqlTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate<String ,Object> redisTemplate;
    @Test
    public void connect() {
        List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
        System.out.println("数据库表列表：" + tables);
        System.out.println("MySQL 连接成功！");
    }

    @Test
    public void redisTest() {
        redisTemplate.opsForValue().set("test","Hello redis");
        Object value = redisTemplate.opsForValue().get("test");
        System.out.println("redis value: " + value);
        System.out.println("Redis 测试成功！");
    }

}
