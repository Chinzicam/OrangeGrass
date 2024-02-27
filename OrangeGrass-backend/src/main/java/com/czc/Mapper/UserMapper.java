package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.User;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.DTO.User2FolderDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    public int insertUserFolder(User2FolderDTO vo);
    public int insertUserFile(User2FileDTO vo);
}
