package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.AvatarEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AvatarMapper extends BaseMapper<AvatarEntity> {

    public AvatarEntity selectRecentAvatar(String userId);

}
