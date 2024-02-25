package com.czc.Mapper;

import com.czc.Entity.FileEntity;
import com.czc.Entity.DTO.User2FileDTO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface RecycleMapper {

    public User2FileDTO selectU2F(String u2fId);

    public FileEntity selectFileByU2FId(String u2fId);

    public int recycleFile(String fileId);

    public int deleteFile(String fileId);

}
