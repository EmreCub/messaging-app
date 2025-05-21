package com.example.mobileapp.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.mobileapp.models.Group;

public interface GroupRepository extends MongoRepository<Group, String> {
    Group findByGroupId(String groupId); // Query to find a group by its custom ID
}
