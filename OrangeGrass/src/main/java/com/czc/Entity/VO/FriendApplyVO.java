package com.czc.Entity.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class FriendApplyVO {
    private String friendApplyRecordId;
    private String fromUserId;
    private String fromUsername;
    private String targetUserId;
    private String targetUsername;
    private String applyReason;
    private Date applyTime;
    private int applyResult;
}
