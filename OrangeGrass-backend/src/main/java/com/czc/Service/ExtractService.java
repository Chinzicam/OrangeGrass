package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Entity.ExtractRecord;
import com.czc.Entity.VO.ExtractRecordVO;
import com.czc.Constant.HttpResonse;

import java.util.List;

public interface ExtractService extends IService<ExtractRecord> {

    public HttpResonse extractFile(String userId, String shareId, String extractCode);

    public List<ExtractRecordVO> getExtractRecordVOByUserId(String userId);
}
