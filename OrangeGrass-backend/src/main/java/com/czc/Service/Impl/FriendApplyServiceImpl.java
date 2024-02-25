package com.czc.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czc.Entity.FriendApplyRecord;
import com.czc.Entity.VO.FriendApplyVO;
import com.czc.Mapper.FriendApplyMapper;
import com.czc.Service.FriendApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendApplyServiceImpl extends ServiceImpl<FriendApplyMapper, FriendApplyRecord> implements FriendApplyService {

    @Autowired
    private FriendApplyMapper friendApplyMapper;

    public List<FriendApplyVO> getFriendApplyVO(String fromUserId, String targetUserId) {
        return friendApplyMapper.selectFriendApplyRecordVO(fromUserId,targetUserId);
    }
}
