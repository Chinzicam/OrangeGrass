package com.czc.Entity.DTO;


import lombok.Data;

import java.util.Date;

@Data
public class EntityListDTO {
    private String f2xId;
    private String type;
    private String folderName;
    private String fileName;
    private String fileId;
    private Long fileSize;
    private int isBan;
    private Date fileCreateTime;
    private Date fileUpdateTime;
    private Date folderCreateTime;
    private Date folderUpdateTime;
}
