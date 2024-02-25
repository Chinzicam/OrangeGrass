package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("friend_apply_record")
public class FriendApplyRecord {
    @TableId("friend_apply_record_id")
    private String friendApplyRecordId;
    @TableField("from_user_id")
    private String fromUserId;
    @TableField("target_user_id")
    private String targetUserId;
    @TableField("apply_reason")
    private String applyReason;
    @TableField("apply_time")
    private Date applyTime;
    @TableField("apply_result")
    private int applyResult;
}
