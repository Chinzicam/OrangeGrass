package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Service.RecycleService;
import com.czc.Service.VoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recycle")
public class RecycleController {

    @Autowired
    private VoService voService;
    @Autowired
    private RecycleService recycleService;

    @CostTime
    @GetMapping("")
    public HttpResonse getRecycleBinItems(@Param("userId") String userId) {
        return HttpResonse.success().setMsg("查询回收站成功").setData(voService.getDeletedFiles(userId));
    }

    @CostTime
    @PutMapping("")
    public HttpResonse recoverFile(@Param("fileId") String fileId) {
        if (recycleService.recycleFile(fileId)) {
            return HttpResonse.success().setMsg("恢复文件成功");
        }
        return HttpResonse.fail().setMsg("恢复文件失败");
    }


    @CostTime
    @DeleteMapping("")
    public HttpResonse deleteFile(@Param("fileId") String fileId) {
        if (recycleService.removeFile(fileId)) {
            return HttpResonse.success().setMsg("删除文件成功");
        }
        return HttpResonse.fail().setMsg("删除文件失败");
    }
}
