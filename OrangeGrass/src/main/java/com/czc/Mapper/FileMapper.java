package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ThumbnailEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface FileMapper extends BaseMapper<FileEntity> {

    public List<FileEntity> selectUncheckedImages();

    public List<FileEntity> selectCheckedImages();

    public List<FileEntity> selectByFolderId(String folderId);

    public FileEntity selectByMd5(String md5);

    public ThumbnailEntity selectThumbnailByPicId(String id);

    public List<FileEntity> selectByUserId(String userId);
}
