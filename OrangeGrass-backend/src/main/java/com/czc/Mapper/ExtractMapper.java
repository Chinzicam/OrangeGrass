package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.ExtractRecord;
import com.czc.Entity.VO.ExtractRecordVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExtractMapper extends BaseMapper<ExtractRecord> {

    public List<ExtractRecordVO> selectExtractRecordVO(String userId);

}
