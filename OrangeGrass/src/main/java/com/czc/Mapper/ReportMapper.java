package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.ReportRecord;
import com.czc.Entity.VO.AppealRecordVO;
import com.czc.Entity.VO.ReportRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper extends BaseMapper<ReportRecord> {

    public List<ReportRecordVO> selectReportVO(int result, String userId, String operatorId);
    public List<ReportRecordVO> selectReportVO(String userId, String operatorId);
    public List<ReportRecordVO> selectCheckedReportVO(String userId, String operatorId);
}
