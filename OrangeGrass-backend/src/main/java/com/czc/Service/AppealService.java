package com.czc.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.czc.Entity.AppealRecord;
import com.czc.Entity.VO.AppealRecordVO;

public interface AppealService extends IService<AppealRecord> {
    public PageInfo<AppealRecordVO> getCheckedAppealRecordVO(String userId, String operatorId,Integer pageNum,Integer pageSize);
    public PageInfo<AppealRecordVO> getAppealRecordVO(int result, String userId, String operatorId, Integer pageNum, Integer pageSize);
    public PageInfo<AppealRecordVO> getAppealRecordVO(String userId, String operatorId,Integer pageNum,Integer pageSize);

}
