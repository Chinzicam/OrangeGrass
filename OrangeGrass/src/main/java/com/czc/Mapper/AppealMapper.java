package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.AppealRecord;
import com.czc.Entity.VO.AppealRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AppealMapper extends BaseMapper<AppealRecord> {
    public List<AppealRecordVO> selectAppealVO(int result, String userId, String operatorId);
    public List<AppealRecordVO> selectAppealVO(String userId, String operatorId);
    public List<AppealRecordVO> selectCheckedAppealVO(String userId,String operatorId);
}
