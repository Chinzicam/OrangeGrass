package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.czc.Entity.ReportRecord;
import com.czc.Entity.VO.ReportRecordVO;

public interface ReportService extends IService<ReportRecord> {
    public PageInfo<ReportRecordVO> getReportRecordVO(int result, String userId, String operatorId, Integer pageNum, Integer pageSize);
    public PageInfo<ReportRecordVO> getReportRecordVO(String userId, String operatorId,Integer pageNum,Integer pageSize);
    public PageInfo<ReportRecordVO> getCheckedReportRecordVO(String userId, String operatorId, Integer pageNum, Integer pageSize);
}
