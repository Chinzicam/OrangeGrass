package com.czc.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ShareRecord;
import com.czc.Entity.VO.ShareRecordVO;
import com.czc.Mapper.ShareMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface ShareService extends IService<ShareRecord> {

    public int checkShareStatus(String shareId);

    public List<ShareRecordVO> getShareRecordVOByUserId(String userId);

    public boolean saveFile(String userId, String folderId, String shareId);

    public HttpResonse extractFile(String shareId, String extractCode);

    /**
     *
     * @param u2fId
     * @param autoGenerateEnable
     * @param extractCodeNeeded
     * @param extractCode
     * @return
     */
    public Map<String,String> shareFile(String u2fId,
                                        boolean autoGenerateEnable,
                                        boolean extractCodeNeeded,
                                        String extractCode);

    public String generateShareRecordWithoutExtractCode(String u2fId);

    public String generateExtractCode();
}
