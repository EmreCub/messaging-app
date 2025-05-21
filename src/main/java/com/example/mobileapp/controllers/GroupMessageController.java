package com.example.mobileapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mobileapp.DTO.MemberRequest;
import com.example.mobileapp.models.Group;
import com.example.mobileapp.models.GroupMessage;
import com.example.mobileapp.services.GroupMessageService;
import com.example.mobileapp.services.GroupService;

@RestController
@RequestMapping("/api/groups/messages")
public class GroupMessageController {

    @Autowired
    private GroupMessageService groupMessageService;

    @Autowired
    private GroupService groupService;

    // Endpoint to send a group message
    @PostMapping("/send")
    public ResponseEntity<GroupMessage> sendGroupMessage(@RequestBody GroupMessage message) {
        try {
            GroupMessage savedMessage = groupMessageService.sendGroupMessage(
                message.getGroupId(),
                message.getSenderId(),
                message.getContent()
            );
            return ResponseEntity.ok(savedMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Endpoint to retrieve all messages for a specific group
    @GetMapping("/{groupId}")
    public ResponseEntity<List<GroupMessage>> getGroupMessages(@PathVariable String groupId) {
        List<GroupMessage> messages = groupMessageService.getMessagesByGroupId(groupId);
        return ResponseEntity.ok(messages);
    }

    // Endpoint to add a member to a group
    @PostMapping("/{groupId}/add-member")
    public ResponseEntity<Group> addMemberToGroup(
            @PathVariable String groupId,
            @RequestBody MemberRequest memberRequest) {
        try {
            Group updatedGroup = groupService.addMemberToGroup(groupId, memberRequest.getMemberId());
            return ResponseEntity.ok(updatedGroup);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
