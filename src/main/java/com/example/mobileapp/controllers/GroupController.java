package com.example.mobileapp.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mobileapp.User;
import com.example.mobileapp.UserRepository;
import com.example.mobileapp.models.Group;
import com.example.mobileapp.models.GroupMessage;
import com.example.mobileapp.services.GroupMessageService;
import com.example.mobileapp.services.GroupService;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMessageService groupMessageService;
    @Autowired
    private UserRepository userRepository;


    // Endpoint to create a new group
    @PostMapping("/create")
public ResponseEntity<Group> createGroup(@RequestBody Group groupRequest) {
    try {
        // Assuming the first member in the list is the creator
        String creatorId = groupRequest.getMembers() != null && !groupRequest.getMembers().isEmpty()
                ? groupRequest.getMembers().get(0)
                : null;

        if (creatorId == null || creatorId.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Group group = groupService.createGroup(
            groupRequest.getName(),
            groupRequest.getGroupId(),
            groupRequest.getMembers(),
            creatorId
        );

        return ResponseEntity.ok(group);
    } catch (IllegalArgumentException e) {
        System.err.println("ERROR: " + e.getMessage());
        return ResponseEntity.badRequest().body(null);
    }
}


    // Endpoint to get a group by its ID
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable String groupId) {
        groupId = groupId.trim();
        Optional<Group> group = groupService.getGroupById(groupId);
        return group.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint to retrieve members of a group
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<String>> getGroupMembers(@PathVariable String groupId) {
        groupId = groupId.trim(); // Ensure groupId is clean
        Optional<Group> group = groupService.getGroupById(groupId); // Fetch the group using groupId
        if (group.isPresent()) {
            List<String> members = group.get().getMembers(); // Retrieve members from the group
            if (!members.isEmpty()) {
                return ResponseEntity.ok(members); // Return members if found
            }
            System.out.println("DEBUG: No members found for groupId: " + groupId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // No members found
        } else {
            System.out.println("DEBUG: Group not found for groupId: " + groupId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Group not found
        }
    }
    
    
    
    // Endpoint to add a member to a group
    @PostMapping("/{groupId}/add-member")
    public ResponseEntity<?> addMemberToGroup(@PathVariable String groupId, @RequestBody String memberId) {
        groupId = groupId.trim(); // Ensure groupId is clean
        memberId = memberId.trim(); // Ensure memberId is clean
    
        System.out.println("DEBUG: Received groupId: " + groupId);
        System.out.println("DEBUG: Received memberId: " + memberId);
    
        // Fetch the group using groupId
        Optional<Group> groupOptional = groupService.getGroupById(groupId);
    
        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
    
            // Check if the member is already part of the group
            if (group.getMembers().contains(memberId)) {
                System.out.println("DEBUG: Member already exists in groupId: " + groupId);
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Member already exists");
            }
    
            // Add the member to the group
            try {
                group.getMembers().add(memberId); // Add the member to the group
                Group updatedGroup = groupService.updateGroup(group); // Save the updated group
                return ResponseEntity.ok(updatedGroup);
            } catch (Exception e) {
                System.out.println("DEBUG: Error adding member to groupId " + groupId + ": " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            }
        } else {
            System.out.println("DEBUG: Group not found for groupId: " + groupId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
        }
    }
    
    
    // Endpoint to send a group message
    @PostMapping("/{groupId}/send")
    public ResponseEntity<GroupMessage> sendGroupMessage(
            @PathVariable String groupId,
            @RequestBody GroupMessage messageRequest) {
        try {
            groupId = groupId.trim();
            GroupMessage savedMessage = groupMessageService.sendGroupMessage(
                    groupId,
                    messageRequest.getSenderId().trim(),
                    messageRequest.getContent());
            return ResponseEntity.ok(savedMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint to retrieve all messages for a specific group
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<GroupMessage>> getGroupMessages(@PathVariable String groupId) {
        groupId = groupId.trim();
        List<GroupMessage> messages = groupMessageService.getMessagesByGroupId(groupId);
        return ResponseEntity.ok(messages);
    }

    // Endpoint to get all groups where the user is a member
    @GetMapping
public ResponseEntity<List<Group>> getUserGroups(@RequestParam String userId) {
    userId = userId.trim(); // Ensure userId is clean

    try {
        final String finalUserId = userId; // Declare final variable

        // Fetch all groups where the user is a member
        List<Group> userGroups = groupService.getAllGroups().stream()
                .filter(group -> group.getMembers().contains(finalUserId))
                .collect(Collectors.toList());

        // Return the list of groups
        return ResponseEntity.ok(userGroups);
    } catch (Exception e) {
        System.err.println("ERROR: Failed to fetch groups for userId " + userId + " - " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
// Endpoint to fetch group details by ID
@GetMapping("/{groupId}/details")
public ResponseEntity<?> getGroupDetails(@PathVariable String groupId) {
    try {
        groupId = groupId.trim();
        Optional<Group> groupOptional = groupService.getGroupById(groupId);

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();

            // Assuming you have a UserRepository to fetch user details
            List<Map<String, String>> memberDetails = group.getMembers().stream()
                    .map(memberId -> {
                        User user = userRepository.findById(memberId).orElse(null); // Fetch user details
                        if (user != null) {
                            Map<String, String> details = new HashMap<>();
                            details.put("id", user.getId());
                            details.put("name", user.getFirstName() + " " + user.getLastName());
                            details.put("email", user.getEmail());
                            return details;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // Construct response
            Map<String, Object> response = new HashMap<>();
            response.put("groupId", group.getGroupId());
            response.put("name", group.getName());
            response.put("creationTime", group.getCreationTime());
            response.put("members", memberDetails);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
        }
    } catch (Exception e) {
        System.err.println("ERROR: Failed to fetch group details - " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
    }
}




    
}
