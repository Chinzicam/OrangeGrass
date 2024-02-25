package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.ShareRecord;
import com.czc.Entity.VO.ShareRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShareMapper extends BaseMapper<ShareRecord> {

    public List<ShareRecordVO> selectShareRecordVOByUserId(String userId);

}
