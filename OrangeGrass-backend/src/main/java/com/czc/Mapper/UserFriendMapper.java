package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.UserFriendRecord;
import com.czc.Entity.VO.FriendVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFriendMapper extends BaseMapper<UserFriendRecord> {

    public List<FriendVO> selectFriendVOList(String userId);

}
