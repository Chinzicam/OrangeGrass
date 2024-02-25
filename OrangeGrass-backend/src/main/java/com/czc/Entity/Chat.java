package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("chat")
public class Chat {
    @TableId("chat_id")
    private int chatId;
    @TableField("from_user_id")
    private String fromUserId;
    @TableField("target_user_id")
    private String targetUserId;
    @TableField("content_type")
    private int contentType;
    @TableField("content")
    private String content;
    @TableField("sent_time")
    private Date sentTime;
    @TableField("is_ban")
    private int isBan;
    @TableField("unread")
    private int unread;
}
