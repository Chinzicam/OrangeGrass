package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_friend")
public class UserFriendRecord {
    @TableId("id")
    private int id;
    @TableField("user_id")
    private String userId;
    @TableField("friend_id")
    private String friendId;
    @TableField("create_time")
    private Date createTime;
}
