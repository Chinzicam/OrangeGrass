package com.czc.Entity.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
public class FileDto {
    private String userId;
    private String folderId;
    private MultipartFile file;
    private Date createTime;

    public FileDto() {
    }

    public FileDto(String userId, String folderId, MultipartFile file, Date createTime) {
        this.userId = userId;
        this.folderId = folderId;
        this.file = file;
        this.createTime = createTime;
    }
}
