package com.newcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHashes() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");


        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 105);
        redisTemplate.opsForList().leftPush(redisKey, 106);
        redisTemplate.opsForList().leftPush(redisKey, 104);
        redisTemplate.opsForList().leftPush(redisKey, "101a");

        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 3));

        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
    }

    @Test
    public void testSets() {
        String redisKey = "test:teachers";
//        redisTemplate.opsForSet().add(redisKey, "刘备","刘备", "关羽", "张飞", "陈闽");
        redisTemplate.expire("test:teacher", Duration.ofMillis(500));
//        System.out.println(redisTemplate.opsForSet().size(redisKey));
//        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        System.out.println(redisTemplate.opsForSet().members(redisKey));

    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "张三", 80);
        redisTemplate.opsForZSet().add(redisKey, "李四", 80);
        redisTemplate.opsForZSet().add(redisKey, "王五", 80.5);
        redisTemplate.opsForZSet().add(redisKey, "陆六", 79.5);
        redisTemplate.opsForZSet().add(redisKey, "陈闽", 99);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "陈闽"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "陆六"));
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 6));


    }

    @Test
    public void testKey() {
        redisTemplate.delete("test:teachers");
        System.out.println(redisTemplate.hasKey("test:teachers"));
        redisTemplate.expire("test:user", Duration.ofSeconds(50));

    }

    // 多次访问同一个key,使用绑定操作BoundValueOperations
    @Test
    public void testBoundoperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    // 编程式事务
    @Test
    public void testTransactional(){
        Object obj=redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey="test:tx";
                // 启动事务
                redisOperations.multi();

                redisOperations.opsForSet().add(redisKey,"zhangsan");
                redisOperations.opsForSet().add(redisKey,"lisi");
                redisOperations.opsForSet().add(redisKey,"luliu");

                System.out.println(redisOperations.opsForSet().members(redisKey));

                //提交事务
                return redisOperations.exec();
            }
        });
        System.out.println(obj);
    }
}
