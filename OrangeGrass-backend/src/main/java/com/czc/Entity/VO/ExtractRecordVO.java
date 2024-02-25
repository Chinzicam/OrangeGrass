package com.czc.Entity.VO;

import lombok.Data;

import java.util.Date;

@Data
public class ExtractRecordVO {
    private String extractRecordId;
    private String shareId;
    private String userId;
    private String username;
    private String fileId;
    private String fileName;
    private String extractCode;
    private String fromUserId;
    private String fromUserName;
    private Date expireTime;
    private Date extractTime;
}
