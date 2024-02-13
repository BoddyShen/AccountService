package com.group.AccountService.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import com.group.AccountService.model.User;
import com.group.AccountService.service.UserService;
import com.group.AccountService.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class AccountKafkaListener {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public AccountKafkaListener(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "requestUserById")
    public void onUserRequest(String userId) {
        
        User user = userService.singleUser(userId);
        System.out.println(user);
        kafkaTemplate.send("responseUserById", user);
    }

    @KafkaListener(topics = "requestUsersByIds")
    public void onUsersRequest(List<String> userIds) {

        List<ObjectId> objectIdList = userIds.stream()
        .map(ObjectId::new)
        .collect(Collectors.toList());

        List<User> users =  userRepository.findAllById(objectIdList);
        kafkaTemplate.send("responseUsersByIds", users);
    }
}