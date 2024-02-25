package com.czc.Entity.VO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EntityTreeVO {
    private String id;
    private String name;
    private String type;
    private Date createTime;
    private Date updateTime;
    private int isBan;
    private String userFileId;
    private String folderFileId;
    private long size;
    private List<EntityTreeVO> subs;
}
