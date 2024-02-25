package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.czc.Entity.DTO.FileDto;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ThumbnailEntity;
import org.apache.tomcat.jni.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileService extends IService<FileEntity> {

    public List<FileEntity> getImages();

    public PageInfo<FileEntity> getCheckedImages(Integer pageNum,Integer pageSize);

    public List<FileEntity> getUncheckedImages();

    public PageInfo<FileEntity> getUncheckedImages(Integer pageNum,Integer pageSize);

    public boolean addFileByMd5(String folderId,String userId,String md5,String fileName);

    public List<FileEntity> getUserFile(String userId);

    public FileEntity getByMD5(String md5);

    public boolean saveRecord(FileDto file,String md5,Long fileSize,String fileName,String filetype);

    public FileEntity getFileByU2Fid(String id);

    public List<FileEntity> selectFilesByFolderId(String folderId);

    public boolean renameFile(String fileId,String newName);

    public ThumbnailEntity getThumbnailByPicId(String id);

    public boolean saveFile(FileDto file) throws Exception;

    public void downloadFile(String u2fId, HttpServletResponse response);

    public void downloadRecycleFile(String u2fId, HttpServletResponse response);

    public void downloadFileForAdmin(String fileId, HttpServletResponse response);

    public boolean isExists(FileEntity fileEntity);

    public void chunkDownload(String u2fid , HttpServletResponse response, HttpServletRequest request);

    public List<FileEntity> selectFilesByUserId(String userId);
}
