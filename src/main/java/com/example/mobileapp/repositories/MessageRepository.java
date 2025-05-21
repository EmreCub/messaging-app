package com.example.mobileapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.mobileapp.models.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
    @Query("{ '$or': [ { 'senderId': ?0, 'receiverId': ?1 }, { 'senderId': ?1, 'receiverId': ?0 } ] }")
    List<Message> findConversation(String user1, String user2);


    List<Message> findBySenderIdAndReceiverId(String senderId, String receiverId);
    List<Message> findByReceiverIdAndSenderId(String receiverId, String senderId);
}
