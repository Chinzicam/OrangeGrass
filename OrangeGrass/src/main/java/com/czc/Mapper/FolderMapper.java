package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.FolderEntity;
import com.czc.Entity.DTO.Folder2XDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FolderMapper extends BaseMapper<FolderEntity> {

    public List<FolderEntity> getByUserId(String userId);

    public int insertFolderX(Folder2XDTO vo);
}
