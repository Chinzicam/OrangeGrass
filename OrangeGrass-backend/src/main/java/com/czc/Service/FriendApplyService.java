package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Entity.FriendApplyRecord;
import com.czc.Entity.VO.FriendApplyVO;

import java.util.List;


public interface FriendApplyService extends IService<FriendApplyRecord> {

    public List<FriendApplyVO> getFriendApplyVO(String fromUserId,String targetUserId);
}
