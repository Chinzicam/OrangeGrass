package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Service.ChatService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @CostTime
    @GetMapping("/check")
    public HttpResonse getUnreadMessagesNumber(@Param("userId") String userId) {
        return HttpResonse.success().setMsg("查询未读消息成功")
                .setData(chatService.getUnreadMessageNumber(userId));
    }

    @CostTime
    @GetMapping("")
    public HttpResonse getChat(@Param("userId") String userId,
                               @Param("friendId") String friendId){
        return HttpResonse.success().setMsg("查询聊天成功")
                .setData(chatService.getMessages(userId,friendId));
    }

    @CostTime
    @PostMapping("")
    public HttpResonse sendMessage(@Param("userId") String userId,
                                   @Param("friendId") String friendId,
                                   @Param("contentType") int contentType,
                                   @Param("content") String content) {
        if (chatService.sendMessage(userId,friendId,contentType,content)) {
            return HttpResonse.success().setMsg("发送消息成功");
        }
        return HttpResonse.fail().setMsg("发送消息失败");
    }
}
