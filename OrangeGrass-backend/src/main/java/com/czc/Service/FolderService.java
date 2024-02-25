package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.FolderEntity;
import com.czc.Constant.HttpResonse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FolderService extends IService<FolderEntity> {

    public boolean isExistsInThisFolder(String folderId, MultipartFile file);

    public boolean renameFolder(String folderId,String newName);

    public List<FolderEntity> selectFoldersByUserId(String userId);

    public boolean addFile(Folder2XDTO vo);

    public boolean isHisFolder(String userId, String folderId);

    public HttpResonse addFolderForUser(String userId,String folderId,FolderEntity folder);
}
