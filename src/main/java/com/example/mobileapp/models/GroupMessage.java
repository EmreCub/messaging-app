package com.example.mobileapp.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "group_messages") // This specifies the MongoDB collection
public class GroupMessage {

    @Id
    private String id; // Unique MongoDB ID
    private String groupId; // The ID of the group the message belongs to
    private String senderId; // ID of the user who sent the message
    private String content; // Message content
    private Date timestamp; // Timestamp of the message

    // Constructors
    public GroupMessage() {}

    public GroupMessage(String groupId, String senderId, String content, Date timestamp) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
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

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GroupMessage{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
