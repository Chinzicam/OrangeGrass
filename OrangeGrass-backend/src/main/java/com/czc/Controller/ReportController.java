package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ReportRecord;
import com.czc.Service.FileService;
import com.czc.Service.ReportService;
import com.czc.Constant.HttpResonse;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.czc.Constant.AppealConstant.*;
import static com.czc.Constant.NSFW.NSFW_BAN;
import static com.czc.Constant.NSFW.NSFW_NOT_BAN;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private FileService fileService;

    @Autowired
    private ReportService reportService;

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/unchecked")
    public HttpResonse getUncheckedReportRecord(@Param("pageNum") Integer pageNum,
                                                @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询审核记录成功")
                .setData(reportService.getReportRecordVO(APPEAL_UNCHECKED,null,null,pageNum,pageSize));
    }

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/checked")
    public HttpResonse getCheckedReportRecord(@Param("pageNum") Integer pageNum,
                                              @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询审核记录成功")
                .setData(reportService.getCheckedReportRecordVO(null,null,pageNum,pageSize));
    }

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("")
    public HttpResonse getAllReportRecord(@Param("pageNum") Integer pageNum,
                                          @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询审核记录成功")
                .setData(reportService.getReportRecordVO(null,null,pageNum,pageSize));
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("")
    public HttpResonse setResult(@Param("result") int result,
                                 @Param("reportId") String reportId,
                                 @Param("operatorId") String operatorId) {
        ReportRecord reportRecord = reportService.getById(reportId);
        reportRecord.setResult(result);
        reportRecord.setOperatorId(operatorId);
        reportRecord.setOperateTime(new Date());
        if (reportService.updateById(reportRecord)) {
            if (result == APPEAL_REJECT) {
                FileEntity file = fileService.getById(reportRecord.getFileId());
                file.setIsBan(NSFW_BAN);
                if (fileService.updateById(file)) {
                    return HttpResonse.success().setMsg("封禁成功");
                }
                return HttpResonse.fail().setMsg("封禁文件时出现错误，请重试");
            }
            if (result == APPEAL_PASS) {
                FileEntity file = fileService.getById(reportRecord.getFileId());
                file.setIsBan(NSFW_NOT_BAN);
                if (fileService.updateById(file)) {
                    return HttpResonse.success().setMsg("解封成功");
                }
                return HttpResonse.fail().setMsg("解封文件时出现错误，请重试");
            }
        }
        return HttpResonse.fail().setMsg("操作失败");
    }

    @CostTime
    @PostMapping("")
    public HttpResonse saveReportRecord(@Param("fileId") String fileId,
                                        @Param("userId") String userId) {
        FileEntity file = fileService.getFileByU2Fid(fileId);
        ReportRecord record = new ReportRecord();
        record.setFileId(file.getId());
        record.setUserId(userId);
        record.setReportTime(new Date());
        if (reportService.save(record)) {
            return HttpResonse.success().setMsg("举报成功");
        }
        return HttpResonse.fail().setMsg("举报失败，请重新举报");
    }
}
