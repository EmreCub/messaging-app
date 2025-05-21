package com.example.mobileapp.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mobileapp.UserRepository;
import com.example.mobileapp.models.FriendRequest;
import com.example.mobileapp.repositories.FriendRequestRepository;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, String>>> searchUsers(@RequestParam String query) {
        String regex = "(?i).*" + query + ".*"; // Case-insensitive regex
    
        List<Map<String, String>> results = userRepository
                .findByFirstNameRegexIgnoreCaseOrLastNameRegexIgnoreCaseOrEmailRegexIgnoreCase(regex, regex, regex)
                .stream()
                .map(user -> Map.of(
                        "id", user.getId(),
                        "name", user.getFullName(), // Use combined full name
                        "email", user.getEmail()
                ))
                .collect(Collectors.toList());
    
        return ResponseEntity.ok(results);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> sendFriendRequest(@RequestBody FriendRequest request) {
        if (!userRepository.existsById(request.getSender())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Sender does not exist!"));
        }
        if (!userRepository.existsById(request.getReceiver())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Receiver does not exist!"));
        }

        if (friendRequestRepository.findBySenderAndReceiver(request.getSender(), request.getReceiver()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Friend request already exists!"));
        }

        request.setStatus("PENDING");
        request.setTimestamp(new Date());
        friendRequestRepository.save(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Friend request sent successfully!"));
    }

    @PostMapping("/accept")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(@RequestBody FriendRequest request) {
        FriendRequest existingRequest = friendRequestRepository.findBySenderAndReceiver(request.getSender(), request.getReceiver());

        if (existingRequest == null || !"PENDING".equals(existingRequest.getStatus())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Friend request not found or already handled!"));
        }

        existingRequest.setStatus("ACCEPTED");
        friendRequestRepository.save(existingRequest);

        return ResponseEntity.ok(Map.of("message", "Friend request accepted successfully!"));
    }

    @GetMapping
    public ResponseEntity<?> getPendingRequests(@RequestParam String userId) {
        userId = userId.trim();

        if (!userRepository.existsById(userId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found!"));
        }

        try {
            List<FriendRequest> receivedRequests = friendRequestRepository.findByReceiverAndStatus(userId, "PENDING");

            List<Map<String, String>> pendingRequests = receivedRequests.stream()
                .map(request -> {
                    Map<String, String> senderDetails = userRepository.findById(request.getSender())
                            .map(user -> Map.of(
                                    "senderId", user.getId(),
                                    "senderEmail", user.getEmail(),
                                    "senderName", user.getFullName())) // Combine first and last name
                            .orElse(Map.of(
                                    "senderId", "Unknown",
                                    "senderEmail", "Unknown",
                                    "senderName", "Unknown"));
                    return Map.of(
                            "id", request.getId(),
                            "senderId", senderDetails.get("senderId"),
                            "senderEmail", senderDetails.get("senderEmail"),
                            "senderName", senderDetails.get("senderName") // Include sender's full name
                    );
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(pendingRequests);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch friend requests - " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch friend requests!"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllFriends(@RequestParam String userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        try {
            // Fetch accepted friend requests where the user is either the sender or receiver
            List<FriendRequest> sentRequests = friendRequestRepository.findBySenderAndStatus(userId, "ACCEPTED");
            List<FriendRequest> receivedRequests = friendRequestRepository.findByReceiverAndStatus(userId, "ACCEPTED");

            // Combine the two lists to get all friend IDs
            List<String> friendIds = sentRequests.stream()
                    .map(FriendRequest::getReceiver)
                    .collect(Collectors.toList());

            friendIds.addAll(receivedRequests.stream()
                    .map(FriendRequest::getSender)
                    .collect(Collectors.toList()));

            // Retrieve friend details
            List<Map<String, String>> friends = friendIds.stream()
                    .map(friendId -> userRepository.findById(friendId).map(friend -> Map.of(
                            "id", friend.getId(),
                            "name", friend.getFirstName() + " " + friend.getLastName(),
                            "email", friend.getEmail()
                    )).orElse(Map.of("id", friendId, "name", "Unknown", "email", "Unknown email")))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to fetch friends - " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
