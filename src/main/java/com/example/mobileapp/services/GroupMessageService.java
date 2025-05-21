package com.example.mobileapp.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mobileapp.models.GroupMessage;
import com.example.mobileapp.repositories.GroupMessageRepository;

@Service
public class GroupMessageService {

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    public GroupMessage sendGroupMessage(String groupId, String senderId, String content) {
        GroupMessage message = new GroupMessage();
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setTimestamp(new Date());

        return groupMessageRepository.save(message);
    }

    public List<GroupMessage> getMessagesByGroupId(String groupId) {
        return groupMessageRepository.findByGroupId(groupId);
    }
}
