package com.example.mobileapp.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.mobileapp.models.FriendRequest;

@Repository
public interface FriendRequestRepository extends MongoRepository<FriendRequest, String> {

    List<FriendRequest> findBySender(String sender);

    List<FriendRequest> findByReceiver(String receiver);

    FriendRequest findBySenderAndReceiver(String sender, String receiver);

    // Find friend requests by receiver and status
    List<FriendRequest> findByReceiverAndStatus(String receiver, String status);

    // Find friend requests by sender and status
    List<FriendRequest> findBySenderAndStatus(String sender, String status);

    boolean existsBySenderAndReceiverAndStatus(String senderId, String receiverId, String status);

}
