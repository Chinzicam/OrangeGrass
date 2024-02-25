package com.czc.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czc.Config.WebSocket.WebSocketServer;
import com.czc.Entity.Chat;
import com.czc.Mapper.ChatMapper;
import com.czc.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.czc.Constant.ChatConstant.*;
import static com.czc.Constant.NotifyConstant.NOTIFY_NEW_MESSAGE;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Override
    public int getUnreadMessageNumber(String userId) {
        return chatMapper.selectUnreadMessageNumber(userId);
    }

    @Override
    public boolean notifyFrontend(String targetUserId,
                               Chat chat) {
        Map<String,Object> data = new HashMap<>();
        data.put("notifyType",NOTIFY_NEW_MESSAGE);
        data.put("data",chat);
        try {
            WebSocketServer.sendInfo(JSON.toJSONString(data),targetUserId);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<Chat> getMessages(String userId, String friendId) {
        chatMapper.readMessages(userId,friendId);
        return chatMapper.selectMessages(userId,friendId);
    }

    @Override
    public boolean sendMessage(String fromUserId,
                               String targetUserId,
                               int contentType,
                               String content) {
        Chat message = new Chat();
        //消息为文本类型
        if (contentType == CHAT_CONTENT_TEXT) {
            if ("".equals(content)) {
                return false;
            }
            message.setFromUserId(fromUserId);
            message.setTargetUserId(targetUserId);
            message.setContentType(contentType);
            message.setSentTime(new Date());
            message.setContent(content);
            message.setUnread(1);
            if (!notifyFrontend(targetUserId,message)) {
                return false;
            }
            return save(message);
        }
        //消息为分享类型
        if (contentType == CHAT_CONTENT_SHARE) {
            message.setFromUserId(fromUserId);
            message.setTargetUserId(targetUserId);
            message.setContentType(contentType);
            message.setSentTime(new Date());
            message.setContent(content);
            message.setUnread(1);
            if (!notifyFrontend(targetUserId,message)) {
                return false;
            }
            return save(message);
        }
        //消息为分享类型
        if (contentType == CHAT_CONTENT_EMOJI) {
            message.setFromUserId(fromUserId);
            message.setTargetUserId(targetUserId);
            message.setContentType(contentType);
            message.setSentTime(new Date());
            message.setContent(content);
            message.setUnread(1);
            if (!notifyFrontend(targetUserId,message)) {
                return false;
            }
            return save(message);
        }
        return false;
    }
}
