package com.czc.Entity.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
public class AppealRecordVO {
    private int appealId;
    private String fileId;
    private String fileName;
    private String userId;
    private String userName;
    private String operatorId;
    private String operatorName;
    private Date appealTime;
    private Date operateTime;
    private int result;
    private String remarks;
}
