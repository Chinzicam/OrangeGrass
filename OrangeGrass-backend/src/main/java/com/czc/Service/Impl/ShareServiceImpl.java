package com.czc.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ShareRecord;
import com.czc.Entity.VO.ShareRecordVO;
import com.czc.Mapper.ShareMapper;
import com.czc.Service.*;
import com.czc.Constant.HttpResonse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.czc.Constant.NSFW.NSFW_BAN;
import static com.czc.Constant.ShareConstant.*;

/**
 * @author Hanaue
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, ShareRecord> implements ShareService {

    @Autowired
    private VoService voService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private FileService fileService;

    @Override
    public int checkShareStatus(String shareId) {
        ShareRecord record = getById(shareId);
        System.out.println(record);
        FileEntity file = fileService.getById(record.getXId());
        System.out.println(file);
        if (NSFW_BAN == file.getIsBan()) {
            return SHARE_STATUS_BANNED;
        }
        return record.getStatus();
    }

    @Override
    public List<ShareRecordVO> getShareRecordVOByUserId(String userId) {
        return shareMapper.selectShareRecordVOByUserId(userId);
    }

    /**
     *
     * @param userId
     * @param folderId
     * @param shareId
     * @return
     */
    @Override
    public boolean saveFile(String userId, String folderId, String shareId){
        ShareRecord shareRecord = getById(shareId);
        User2FileDTO u2f = new User2FileDTO();
        u2f.setFileName(shareRecord.getXName());
        u2f.setFileId(shareRecord.getXId());
        u2f.setUserId(userId);
        u2f.setId(UUID.randomUUID().toString().replaceAll("-",""));
        /**
         * 往用户文件表插
         */
        if (!userService.addFile(u2f)) {
            return false;
        }
        Folder2XDTO f2x = new Folder2XDTO();
        f2x.setId(UUID.randomUUID().toString().replaceAll("-",""));
        f2x.setFolderId(folderId);
        f2x.setXId(u2f.getId());
        f2x.setXType("FILE");
        if (!folderService.addFile(f2x)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param shareId
     * @param extractCode
     * @return
     */
    @Override
    public HttpResonse extractFile(String shareId, String extractCode){
        ShareRecord record = getById(shareId);
        Map<String,String> res = new HashMap<>();
        if (record == null) {
            return HttpResonse.fail().setMsg("分享记录为空");
        }
        if (record.getShareType() == SHARE_FREE) {
            res.put("fileId",record.getXId());
            return HttpResonse.success().setMsg("提取成功").setData(res);
        }
        if (record.getShareType() == SHARE_NEED_EXTRACTCODE) {
            if (record.getExtractCode().equals(extractCode)){
                res.put("fileId",record.getXId());
                return HttpResonse.success().setMsg("提取成功").setData(res);
            } else {
                return HttpResonse.fail().setMsg("提取码错误");
            }
        }
        return HttpResonse.fail().setMsg("提取失败");
    }

    @Override
    public Map<String,String> shareFile(String u2fId,
                                        boolean autoGenerateEnable,
                                        boolean extractCodeNeeded,
                                        String extractCode){
        User2FileDTO u2f = voService.selectUser2FileDTOById(u2fId);
        ShareRecord shareRecord = new ShareRecord();
        if (autoGenerateEnable) {
            extractCode = generateExtractCode();
        }
        if (!extractCodeNeeded) {
            extractCode = null;
            shareRecord.setShareType(SHARE_FREE);
        }
        shareRecord.setShareType(SHARE_NEED_EXTRACTCODE);
        shareRecord.setXName(u2f.getFileName());
        shareRecord.setFromUserId(u2f.getUserId());
        shareRecord.setExtractCode(extractCode);
        shareRecord.setId(UUID.randomUUID().toString().replaceAll("-",""));
        shareRecord.setXId(u2f.getFileId());
        shareRecord.setCreateTime(new Date());
        shareRecord.setXType("FILE");
        Map<String,String> res = new HashMap<>();
        if (this.save(shareRecord)) {
            res.put("shareId",shareRecord.getId());
            res.put("extractCode",shareRecord.getExtractCode());
            return res;
        }
        return null;
    }

    @Override
    public String generateShareRecordWithoutExtractCode(String u2fId){
        User2FileDTO u2f = voService.selectUser2FileDTOById(u2fId);
        ShareRecord record = new ShareRecord();
        record.setShareType(SHARE_FREE);
        record.setCreateTime(new Date());
        record.setFromUserId(u2f.getUserId());
        record.setXId(u2f.getFileId());
        record.setXName(u2f.getFileName());
        return "";
    }

    @Override
    public String generateExtractCode(){
        return UUID.randomUUID().toString().trim().replaceAll("-","").substring(0,4);
    }
}
