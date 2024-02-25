package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@TableName("extract_record")
@Data
public class ExtractRecord {
    @TableId("extract_record_id")
    private String extractRecordId;
    @TableField("share_id")
    private String shareId;
    @TableField("user_id")
    private String userId;
    @TableField("extract_time")
    private Date extractTime;
}
