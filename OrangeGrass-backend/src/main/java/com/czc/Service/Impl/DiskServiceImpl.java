package com.czc.Service.Impl;

import com.czc.Entity.DTO.EntityListDTO;
import com.czc.Entity.FileEntity;
import com.czc.Entity.FolderEntity;
import com.czc.Entity.VO.EntityTreeVO;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Service.DiskService;
import com.czc.Service.FileService;
import com.czc.Service.FolderService;
import com.czc.Service.VoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiskServiceImpl implements DiskService {
    @Autowired
    private FolderService folderService;
    @Autowired
    private FileService fileService;
    @Autowired
    private VoService voService;


    @Override
    public List<EntityTreeVO> getFolderAndFileByFolderId(String folderId) {
        List<EntityListDTO> tmp = voService.getEntityList(folderId);
        List<EntityTreeVO> result = new ArrayList<>();
        if (tmp == null || tmp.size() == 0) {
            return new ArrayList<>();
        }
        for (EntityListDTO e : tmp) {
            EntityTreeVO vo = new EntityTreeVO();
            vo.setId(e.getF2xId());
            vo.setType(e.getType());
            vo.setName(e.getFileName() == null ? e.getFolderName() : e.getFileName());
            vo.setUpdateTime(e.getFileUpdateTime() == null ?
                    e.getFolderUpdateTime() : e.getFileUpdateTime());
            vo.setCreateTime(e.getFileCreateTime() == null ?
                    e.getFolderCreateTime() : e.getFileCreateTime());
            vo.setSize(e.getFileSize() == null ? 0 : e.getFileSize());
            vo.setIsBan(e.getIsBan());
            result.add(vo);
        }
        return result;
    }

    @Override
    public EntityTreeVO createFolderAndFileTreeByType (String userId,String folderId,List<String> typeList){
        List<FolderEntity> myFolders = folderService.selectFoldersByUserId(userId);
        List<FileEntity> myFiles = fileService.selectFilesByUserId(userId);
        List<FileEntity> after = new ArrayList<>();
        List<Folder2XDTO> vo = voService.selectFolder2XDTObyUserId(userId);
        List<User2FileDTO> temp = voService.selectUser2FileDTOByUserId(userId);
        /**
         * 获取头节点
         */
        EntityTreeVO root = new EntityTreeVO();
        if (folderId == null || "".equals(folderId)) {
            for (FolderEntity folder : myFolders){
                if (folder.getIsMain() == 1){
                    root.setId(folder.getFolderId());
                    root.setName(folder.getFolderName());
                    root.setCreateTime(folder.getCreateTime());
                    root.setUpdateTime(folder.getUpdateTime());
                    root.setType("FOLDER");
                    root.setSize(folder.getFolderSize());
                    break;
                }
            }
        } else {
            for (FolderEntity folder : myFolders){
                if (folderId.equals(folder.getFolderId())){
                    root.setId(folder.getFolderId());
                    root.setName(folder.getFolderName());
                    root.setCreateTime(folder.getCreateTime());
                    root.setUpdateTime(folder.getUpdateTime());
                    root.setType("FOLDER");
                    root.setSize(folder.getFolderSize());
                    break;
                }
            }
        }
        /**
         * 根据类型筛选
         */
        for (FileEntity file : myFiles){
            String fileName = file.getFileName();
            if (typeList.contains(fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase())){
                after.add(file);
            }
        }
        dfsForFolderAndFileTree(root,myFolders,after,vo,temp);
        return root;
    }

    @Override
    public EntityTreeVO createFolderTree (String userId){
        List<FolderEntity> myFolders = folderService.selectFoldersByUserId(userId);
        List<Folder2XDTO> vo = voService.selectFolder2XDTObyUserId(userId);
        /**
         * 获取头节点
         */
        EntityTreeVO root = new EntityTreeVO();
        for (FolderEntity folder : myFolders){
            if (folder.getIsMain() == 1){
                root.setId(folder.getFolderId());
                root.setName(folder.getFolderName());
                root.setCreateTime(folder.getCreateTime());
                root.setUpdateTime(folder.getUpdateTime());
                root.setType("FOLDER");
                root.setSize(folder.getFolderSize());
                break;
            }
        }
        /**
         * 通过dfs创造树
         */
        dfsForFolderTree(root,myFolders,vo);
        return root;
    }

    @Override
    public EntityTreeVO createFolderAndFileTree(String userId , String folderId){
        List<FolderEntity> myFolders = folderService.selectFoldersByUserId(userId);
        List<FileEntity> myFiles = fileService.selectFilesByUserId(userId);
        List<Folder2XDTO> vo = voService.selectFolder2XDTObyUserId(userId);
        List<User2FileDTO> u2f = voService.selectUser2FileDTOByUserId(userId);
        /**
         * 获取头节点
         */
        EntityTreeVO root = new EntityTreeVO();
        /**
         * 如果未指定文件夹
         */
        if (folderId == null || "".equals(folderId)) {
            for (FolderEntity folder : myFolders){
                if (folder.getIsMain() == 1){
                    root.setId(folder.getFolderId());
                    root.setName(folder.getFolderName());
                    root.setCreateTime(folder.getCreateTime());
                    root.setUpdateTime(folder.getUpdateTime());
                    root.setType("FOLDER");
                    root.setSize(folder.getFolderSize());
                    break;
                }
            }
        }
        /**
         * 若指定文件夹
         */
        else {
            for (FolderEntity folder : myFolders){
                if (folderId.equals(folder.getFolderId())){
                    root.setId(folder.getFolderId());
                    root.setName(folder.getFolderName());
                    root.setCreateTime(folder.getCreateTime());
                    root.setUpdateTime(folder.getUpdateTime());
                    root.setType("FOLDER");
                    root.setSize(folder.getFolderSize());
                    break;
                }
            }
        }
        /**
         * 通过dfs创造树
         */
        dfsForFolderAndFileTree(root,myFolders,myFiles,vo,u2f);
        return root;
    }

    public void dfsForFolderTree(EntityTreeVO root,
                                        List<FolderEntity> folders,
                                        List<Folder2XDTO> f2xvo){
        if (root == null){
            return;
        }
        addFolder(root, folders, f2xvo);
        /**
         * 没有元素可以加入root的子节点，说明已经到底
         */
        if (root.getSubs() == null){
            return;
        }

        /**
         * 继续遍历root的子节点
         */
        for (EntityTreeVO r : root.getSubs()){
            dfsForFolderTree(r,folders,f2xvo);
        }
    }

    public void dfsForFolderAndFileTree(EntityTreeVO root,
                    List<FolderEntity> folders,
                    List<FileEntity> files,
                    List<Folder2XDTO> f2xvo,
                    List<User2FileDTO> u2fvos){
        if (root == null){
            return;
        }
        addFolder(root, folders, f2xvo);
        addFile(root,f2xvo,u2fvos,files);
        /**
         * 没有元素可以加入root的子节点，说明已经到底
         */
        if (root.getSubs() == null){
            return;
        }

        /**
         * 继续遍历root的子节点
         */
        for (EntityTreeVO r : root.getSubs()){
            dfsForFolderAndFileTree(r,folders,files,f2xvo,u2fvos);
        }
    }

    private void addFile(EntityTreeVO root,
                         List<Folder2XDTO> f2xvo,
                         List<User2FileDTO> u2fvos,
                         List<FileEntity> files){
        for (Folder2XDTO vo : f2xvo){
            if (vo.getFolderId().equals(root.getId())){
                for (User2FileDTO u2fvo : u2fvos) {
                    if (u2fvo.getId().equals(vo.getXId())){
                        EntityTreeVO temp = new EntityTreeVO();
                        for (FileEntity file : files){
                            if (file.getId().equals(u2fvo.getFileId())){
                                temp.setId(u2fvo.getId());
                                temp.setName(u2fvo.getFileName());
                                temp.setType("FILE");
                                temp.setCreateTime(file.getCreateTime());
                                temp.setIsBan(file.getIsBan());
                                temp.setUpdateTime(file.getUpdateTime());
                                temp.setSize(file.getFileSize());
                                break;
                            }
                        }
                        if (temp.getId() != null) {
                            List<EntityTreeVO> tmp = root.getSubs() == null ?
                                    new ArrayList<>() : root.getSubs();
                            tmp.add(temp);
                            root.setSubs(tmp);
                        }
                    }
                }
            }
        }
    }

    private void addFolder(EntityTreeVO root, List<FolderEntity> folders, List<Folder2XDTO> f2xvo) {
        for (Folder2XDTO vo : f2xvo){
            if (vo.getFolderId().equals(root.getId())){
                for (FolderEntity folder : folders){
                    if (vo.getXId().equals(folder.getFolderId())){
                        EntityTreeVO temp = new EntityTreeVO();
                        temp.setSize(folder.getFolderSize());
                        temp.setType("FOLDER");
                        temp.setCreateTime(folder.getCreateTime());
                        temp.setUpdateTime(folder.getUpdateTime());
                        temp.setName(folder.getFolderName());
                        temp.setId(folder.getFolderId());
                        List<EntityTreeVO> tmp = root.getSubs() == null ?
                                new ArrayList<>() : root.getSubs();
                        tmp.add(temp);
                        root.setSubs(tmp);
                    }
                }
            }
        }
    }

}
