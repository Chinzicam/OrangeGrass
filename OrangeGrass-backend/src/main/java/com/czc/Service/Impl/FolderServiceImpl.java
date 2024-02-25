package com.czc.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.DTO.User2FolderDTO;
import com.czc.Entity.FileEntity;
import com.czc.Entity.FolderEntity;
import com.czc.Entity.User;
import com.czc.Mapper.FolderMapper;
import com.czc.Mapper.VoMapper;
import com.czc.Service.FileService;
import com.czc.Service.FolderService;
import com.czc.Service.UserService;
import com.czc.Util.MD5Util;
import com.czc.Constant.HttpResonse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, FolderEntity> implements FolderService {

    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;
    @Autowired
    private VoMapper voMapper;

    /**
     * 重命名文件夹
     * @param folderId
     * @param newName
     * @return
     */
    @Override
    public boolean renameFolder(String folderId,String newName){
        FolderEntity folder = folderMapper.selectById(folderId);
        if (folder == null){
            return false;
        }
        folder.setFolderName(newName);
        folder.setUpdateTime(new Date());
        if (folderMapper.updateById(folder) > 0){
            return true;
        }
        return false;
    }

    /**
     * 查询该目录下有无该文件
     * @param folderId
     * @param file
     * @return
     */
    @Override
    public boolean isExistsInThisFolder(String folderId, MultipartFile file){
        List<FileEntity> filesInThisFolder = fileService.selectFilesByFolderId(folderId);
        try {
            String targetMd5 = MD5Util.calcMD5(file.getInputStream());
            for (FileEntity temp : filesInThisFolder){
                if (temp.getMd5().equals(targetMd5)){
                    return false;
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取用户的文件夹
     * @param userId
     * @return
     */
    @Override
    public List<FolderEntity> selectFoldersByUserId(String userId){
        return folderMapper.getByUserId(userId);
    }

    /**
     * 往文件夹里加文件
     * @param vo
     * @return
     */
    @Override
    public boolean addFile(Folder2XDTO vo){
        if (folderMapper.insertFolderX(vo) > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean isHisFolder(String userId, String folderId){
        List<FolderEntity> myFolders = selectFoldersByUserId(userId);
        boolean flag = false;
        for (FolderEntity f : myFolders){
            if (f.getFolderId().equals(folderId)){
                flag = true;
                return flag;
            }
        }
        return flag;
    }

    @Override
    public HttpResonse addFolderForUser(String userId,String folderId,FolderEntity folderEntity){
        User user = userService.getById(userId);
        FolderEntity folder = folderMapper.selectById(folderId);

        User2FolderDTO ufvo = new User2FolderDTO();
        ufvo.setId(UUID.randomUUID().toString().trim().trim().replaceAll("-",""));
        ufvo.setUserId(userId);
        ufvo.setFolderId(folderEntity.getFolderId());

        Folder2XDTO fxvo = new Folder2XDTO();
        fxvo.setId(UUID.randomUUID().toString().trim().trim().replaceAll("-",""));
        fxvo.setFolderId(folderId);
        fxvo.setXType("FOLDER");
        fxvo.setXId(folderEntity.getFolderId());

        if (folderMapper.insert(folderEntity) > 0
                && voMapper.insertUserFolder(ufvo) > 0
                && folderMapper.insertFolderX(fxvo) > 0) {
            return HttpResonse.success().setMsg("已为用户【" + user.getUsername() + "】"
                    + "在文件夹【" + folder.getFolderName() +
                    "】下添加文件夹【" + folderEntity.getFolderName() + "】");
        }
        return HttpResonse.fail().setMsg("添加文件夹失败");
    }
}
