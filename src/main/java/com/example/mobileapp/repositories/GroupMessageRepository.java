package com.example.mobileapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.mobileapp.models.GroupMessage;

public interface GroupMessageRepository extends MongoRepository<GroupMessage, String> {
    List<GroupMessage> findByGroupId(String groupId); // Find all messages for a specific group
}
