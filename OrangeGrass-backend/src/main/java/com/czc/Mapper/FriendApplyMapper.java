package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.FriendApplyRecord;
import com.czc.Entity.VO.FriendApplyVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FriendApplyMapper extends BaseMapper<FriendApplyRecord> {

    public List<FriendApplyVO> selectFriendApplyRecordVO(String fromUserId,String targetUserId);

}
