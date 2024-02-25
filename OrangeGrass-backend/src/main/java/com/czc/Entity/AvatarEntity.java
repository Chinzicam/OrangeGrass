package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("avatar")
@Data
public class AvatarEntity {

    @TableId("avatar_id")
    private String avatarId;

    @TableField("user_id")
    private String userId;

    @TableField("avatar_location")
    private String avatarLocation;

    @TableField("create_time")
    private Date createTime;
}
