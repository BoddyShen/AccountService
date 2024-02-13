package com.group.AccountService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void cacheUserToken(String token, String userEmail) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("user:JWT:"+token, userEmail, 1, TimeUnit.DAYS);
    }

    public String getUserEmailByToken(String token) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return ops.get(token);
    }

    public void subscribeToUserChannel(String userId) {
        String userChannel = "user_channel:" + userId;

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(stringRedisTemplate.getConnectionFactory());
        container.addMessageListener((message, pattern) -> {
            String msgBody = new String(message.getBody());
            handleMatchUpdate(userId, msgBody);
        }, new ChannelTopic(userChannel));
        container.start();
    }

    private void handleMatchUpdate(String userId, String message) {
        if (message.startsWith("New match with userId: ")) {
            String matchedUserId = message.substring("New match with userId: ".length());
            updateMatchedList(userId, matchedUserId);
        }
    }

    public void updateMatchedList(String userId, String matchedUserId) {
        String key = "match:matchedUsers:" + userId;
        ListOperations<String, String> listOps = stringRedisTemplate.opsForList();
        listOps.rightPush(key, matchedUserId);

        stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
    }
}
