package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("report_record")
public class ReportRecord {
    @TableId("report_id")
    private int reportId;
    @TableField("file_id")
    private String fileId;
    @TableField("user_id")
    private String userId;
    @TableField("operator_id")
    private String operatorId;
    @TableField("report_time")
    private Date reportTime;
    @TableField("operate_time")
    private Date operateTime;
    @TableField("result")
    private int result;
    @TableField("remarks")
    private String remarks;
}
