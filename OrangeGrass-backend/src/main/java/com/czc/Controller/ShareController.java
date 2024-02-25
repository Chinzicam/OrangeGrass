package com.czc.Controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ShareRecord;
import com.czc.Entity.User;
import com.czc.Service.FileService;
import com.czc.Service.ShareService;
import com.czc.Service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    @Autowired
    private UserService userService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private FileService fileService;

    @CostTime
    @GetMapping("/check")
    public HttpResonse checkShareStatus(@Param("shareId") String shareId) {
        int status = shareService.checkShareStatus(shareId);
        return HttpResonse.success().setData(status);
    }

    @CostTime
    @PutMapping("/")
    public HttpResonse statusChange(@Param("shareId") String shareId,
                                    @Param("targetValue") int targetValue) {
        if (shareService.update(new UpdateWrapper<ShareRecord>()
                .set("status",targetValue)
                .eq("share_id",shareId))) {
            return HttpResonse.success().setMsg("修改成功");
        }
        return HttpResonse.fail().setMsg("修改失败");
    }

    @CostTime
    @GetMapping("/user")
    public HttpResonse getMyShareRecord(@Param("userId") String userId) {
        return HttpResonse.success()
                .setData(shareService.getShareRecordVOByUserId(userId))
                .setMsg("查询分享记录成功");
    }

    @CostTime
    @PostMapping("/save")
    public HttpResonse saveFile(@Param("userId") String userId,
                                 @Param("shareId") String shareId,
                                 @Param("folderId") String folderId){
        if (shareService.saveFile(userId,folderId,shareId)) {
            return HttpResonse.success().setMsg("文件保存成功");
        }
        return HttpResonse.fail().setMsg("文件保存失败");
    }

    @CostTime
    @GetMapping("/fileinfo")
    public HttpResonse getFileInfo(@Param("shareId") String shareId) {
        ShareRecord shareRecord = shareService.getById(shareId);
        FileEntity file = fileService.getById(shareRecord.getXId());
        Map<String,String> res = new HashMap<>();
        res.put("filename",file.getFileName());
        res.put("fileid", file.getId());
        res.put("type",shareRecord.getXType());
        return HttpResonse.success().setMsg("查询成功").setData(res);
    }

    /**
     * 提取文件
     * @param shareId
     * @param extractCode
     * @return
     */
    @CostTime
    @GetMapping("/extract")
    public HttpResonse extractFile(@Param("shareId") String shareId,
                                   @Param("extractCode") String extractCode){
        return shareService.extractFile(shareId,extractCode);
    }


    /**
     * 根据u2fid创建分享id并返回
     * 用户通过分享id获取文件
     * @param u2fId
     * @param extractCodeNeeded
     * @param autoGenerateEnable
     * @param extractCode
     * @return
     */
    @CostTime
    @GetMapping("/file")
    public HttpResonse shareThisFile(@Param("u2fId") String u2fId,
                                     /**
                                      * 是否需要提取码
                                      * 1表示需要 SHARE_NEED_EXTRACTCODE
                                      * 2表示不需要 SHARE_FREE
                                      */
                                     @Param("extractCodeNeeded") boolean extractCodeNeeded,
                                     /**
                                     * 是否需要系统生成提取码
                                     * 1表示需要系统自动生成，此时extractCode不起作用
                                     * AUTO_GENERATE_TRUE
                                     *
                                     * 2表示传入的extractCode作为提取码
                                     * AUTO_GENERATE_FALSE
                                     */
                                     @Param("autoGenerateEnable") boolean autoGenerateEnable,
                                     @Param("expireTime") String expireTime,
                                     @Param("extractCode") String extractCode){
        System.out.println(expireTime);
        Map<String,String> res = shareService.shareFile(u2fId,autoGenerateEnable,extractCodeNeeded,extractCode);
        if (res == null && res.size() == 0){
            return HttpResonse.fail().setMsg("出错");
        }
        return HttpResonse.success().setMsg("成功").setData(res);
    }

    @CostTime
    @GetMapping("/")
    public HttpResonse getShareRecordUserName(@Param("shareId") String shareId){
        String userId = shareService.getById(shareId).getFromUserId();
        User user = userService.getById(userId);
        Map<String,String> res = new HashMap<>();
        res.put("username",user.getUsername());
        res.put("userId",user.getUserId());
        return HttpResonse.success().setMsg("查询成功").setData(res);
    }

}
