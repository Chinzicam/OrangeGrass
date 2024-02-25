package com.czc.Entity.VO;

import lombok.Data;

import java.util.Date;

@Data
public class ReportRecordVO {
    private int reportId;
    private String fileId;
    private String fileName;
    private String userId;
    private String userName;
    private String operatorId;
    private String operatorName;
    private Date reportTime;
    private Date operateTime;
    private int result;
    private String remarks;
}
