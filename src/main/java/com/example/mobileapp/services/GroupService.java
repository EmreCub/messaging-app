package com.example.mobileapp.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mobileapp.models.Group;
import com.example.mobileapp.repositories.GroupRepository;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    /**
     * Create a new group
     *
     * @param name     Group name
     * @param groupId  Unique Group ID
     * @param members  List of initial members
     * @return Created Group
     */
    public Group createGroup(String name, String groupId, List<String> members, String creatorId) {
        // Ensure the provided groupId does not already exist
        if (groupRepository.findByGroupId(groupId) != null) {
            throw new IllegalArgumentException("Group ID already exists");
        }
    
        // Generate a unique groupId if not provided
        String generatedGroupId = (groupId != null && !groupId.isEmpty()) ? groupId : UUID.randomUUID().toString();
    
        // Ensure the creator is added to the members list
        if (members == null) {
            members = new ArrayList<>();
        }
        if (!members.contains(creatorId)) {
            members.add(creatorId); // Add the creator to the group
        }
    
        // Create and save the group
        Group group = new Group();
        group.setGroupId(generatedGroupId);
        group.setName(name);
        group.setMembers(members);
        group.setCreationTime(LocalDateTime.now()); // Set the creation time
    
        return groupRepository.save(group);
    }
    
    


    /**
     * Fetch a group by its ID
     *
     * @param groupId Group ID
     * @return Optional<Group>
     */
    public Optional<Group> getGroupById(String groupId) {
        return Optional.ofNullable(groupRepository.findByGroupId(groupId));
    }

    /**
     * Add a member to an existing group
     *
     * @param groupId  Group ID
     * @param memberId Member ID to add
     * @return Updated Group
     */
    public Group addMemberToGroup(String groupId, String memberId) {
        // Fetch the group by its ID
        Group group = groupRepository.findByGroupId(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        // Ensure the member isn't already in the group
        if (group.getMembers().contains(memberId)) {
            throw new IllegalArgumentException("Member is already part of the group");
        }

        // Add the member and save the group
        group.getMembers().add(memberId);
        return groupRepository.save(group);
    }

    /**
     * Fetch all members of a group
     *
     * @param groupId Group ID
     * @return List of Member IDs
     */
    public List<String> getGroupMembers(String groupId) {
        Optional<Group> group = getGroupById(groupId);
        return group.map(Group::getMembers)
                    .orElseThrow(() -> new IllegalArgumentException("Group not found with ID: " + groupId));
    }

    /**
     * Update an existing group
     *
     * @param group Updated group object
     * @return Updated Group
     */
    public Group updateGroup(Group group) {
        return groupRepository.save(group);
    }

    /**
     * Fetch all groups
     *
     * @return List of all Groups
     */
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Fetch all groups for a specific user
     *
     * @param userId User ID
     * @return List of Groups the user is a member of
     */
    public List<Group> getUserGroups(String userId) {
        return groupRepository.findAll().stream()
                              .filter(group -> group.getMembers().contains(userId))
                              .collect(Collectors.toList());
    }
}
