package com.example.mobileapp.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "groups") // This specifies the MongoDB collection
public class Group {

    @Id
    private String id; // Unique MongoDB ID
    private String groupId; // Custom unique ID for the group
    private String name; // Group name
    private List<String> members; // List of member user IDs
    private LocalDateTime creationTime; // New field for storing creation time

    // Constructors
    public Group() {
        this.creationTime = LocalDateTime.now(); // Automatically set creation time for default constructor
    }

    public Group(String groupId, String name, List<String> members) {
        this.groupId = groupId;
        this.name = name;
        this.members = members;
        this.creationTime = LocalDateTime.now(); // Automatically set creation time
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", name='" + name + '\'' +
                ", members=" + members +
                ", creationTime=" + creationTime +
                '}';
    }
}
