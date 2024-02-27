package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.AppealRecord;
import com.czc.Entity.FileEntity;
import com.czc.Service.AppealService;
import com.czc.Service.FileService;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.czc.Constant.AppealConstant.*;
import static com.czc.Constant.NSFW.NSFW_BAN;
import static com.czc.Constant.NSFW.NSFW_NOT_BAN;

@RequestMapping("/api/appeal")
@RestController
public class AppealController{

    @Autowired
    private FileService fileService;

    @Autowired
    private AppealService appealService;

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/unchecked")
    public HttpResonse getUncheckedAppealRecord(@Param("pageNum") Integer pageNum,
                                                @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询审核记录成功")
                .setData(appealService.getAppealRecordVO(APPEAL_UNCHECKED,null,null,pageNum,pageSize));
    }

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/checked")
    public HttpResonse getCheckedAppealRecord(@Param("pageNum") Integer pageNum,
                                              @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询审核记录成功")
                .setData(appealService.getCheckedAppealRecordVO(null,null,pageNum,pageSize));
    }

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("")
    public HttpResonse getAllAppealRecord(@Param("pageNum") Integer pageNum,
                                          @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询审核记录成功")
                .setData(appealService.getAppealRecordVO(null,null,pageNum,pageSize));
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("")
    public HttpResonse setResult(@Param("result") int result,
                                 @Param("appealId") String appealId,
                                 @Param("operatorId") String operatorId) {
        AppealRecord appealRecord = appealService.getById(appealId);
        appealRecord.setResult(result);
        appealRecord.setOperatorId(operatorId);
        appealRecord.setOperateTime(new Date());
        if (appealService.updateById(appealRecord)) {
            if (result == APPEAL_REJECT) {
                FileEntity file = fileService.getById(appealRecord.getFileId());
                file.setIsBan(NSFW_BAN);
                if (fileService.updateById(file)) {
                    return HttpResonse.success().setMsg("驳回成功");
                }
                return HttpResonse.fail().setMsg("封禁文件时出现错误，请重试");
            }
            if (result == APPEAL_PASS) {
                FileEntity file = fileService.getById(appealRecord.getFileId());
                file.setIsBan(NSFW_NOT_BAN);
                if (fileService.updateById(file)) {
                    return HttpResonse.success().setMsg("解封成功");
                }
                return HttpResonse.fail().setMsg("解封文件时出现错误，请重试");
            }
        }
        return HttpResonse.fail().setMsg("审核失败，请重新审核");
    }

    @CostTime
    @PostMapping("")
    public HttpResonse saveAppealRecord(@Param("fileId") String fileId,
                                        @Param("userId") String userId) {
        FileEntity file = fileService.getFileByU2Fid(fileId);
        AppealRecord record = new AppealRecord();
        record.setFileId(file.getId());
        record.setUserId(userId);
        record.setAppealTime(new Date());
        if (appealService.save(record)) {
            return HttpResonse.success().setMsg("申诉成功");
        }
        return HttpResonse.fail().setMsg("申诉失败，请重新申诉");
    }
}
