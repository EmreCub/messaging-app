package com.example.mobileapp.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mobileapp.models.Message;
import com.example.mobileapp.repositories.FriendRequestRepository;
import com.example.mobileapp.repositories.MessageRepository;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FriendRequestRepository friendRequestRepository; // Injected repository

    // Endpoint to send a message
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Message message) {
        try {
            // Ensure sender and receiver IDs are valid
            String senderId = message.getSenderId().trim();
            String receiverId = message.getReceiverId().trim();
    
            // Check if the sender and receiver are friends
            boolean isFriendshipValid = friendRequestRepository.existsBySenderAndReceiverAndStatus(senderId, receiverId, "ACCEPTED")
                    || friendRequestRepository.existsBySenderAndReceiverAndStatus(receiverId, senderId, "ACCEPTED");
    
            if (!isFriendshipValid) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Users must be friends to send messages!");
            }
    
            // Save the message to the database
            message.setTimestamp(new Date());
            messageRepository.save(message);
            System.out.println("DEBUG: Message saved: " + message);
    
            return ResponseEntity.status(HttpStatus.CREATED).body("Message sent successfully!");
        } catch (Exception e) {
            System.out.println("DEBUG: Error saving message - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving message!");
        }
    }
    

    // Endpoint to retrieve messages between two users
    @GetMapping
    public ResponseEntity<List<Message>> getConversation(@RequestParam String user1, @RequestParam String user2) {
        user1 = user1.trim();
        user2 = user2.trim();
        System.out.println("DEBUG: Fetching conversation between user1: " + user1 + " and user2: " + user2);

        try {
            // Retrieve messages using repository methods
            List<Message> senderToReceiver = messageRepository.findBySenderIdAndReceiverId(user1, user2);
            List<Message> receiverToSender = messageRepository.findByReceiverIdAndSenderId(user1, user2);

            // Combine results
            List<Message> conversation = new ArrayList<>();
            conversation.addAll(senderToReceiver);
            conversation.addAll(receiverToSender);

            // Debugging fetched messages
            System.out.println("DEBUG: Messages from user1 to user2: " + senderToReceiver);
            System.out.println("DEBUG: Messages from user2 to user1: " + receiverToSender);

            // Sort messages by timestamp
            conversation.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
            System.out.println("DEBUG: Combined and sorted conversation: " + conversation);

            // Return the response
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            // Log any errors
            System.err.println("ERROR: Failed to fetch conversation - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
