package com.czc.Controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.FileEntity;
import com.czc.Entity.VO.NSFWVO;
import com.czc.Service.FileService;
import com.czc.Service.NSFWService;
import com.czc.Service.SystemService;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.czc.Constant.NSFW.NSFW_BAN;

@RestController
@RequestMapping("/api/nsfw")
public class NSFWController {

    @Autowired
    private NSFWService nsfwService;
    @Autowired
    private FileService fileService;
    @Autowired
    private SystemService systemService;

    @GetMapping("/images")
    public HttpResonse checkUncheckedImages() {
        List<FileEntity> files = fileService.getUncheckedImages();
        try {
            nsfwService.checkImages(files);
        } catch (Exception e){
            e.printStackTrace();
            return HttpResonse.fail().setMsg("图片批量鉴黄失败");
        }
        return HttpResonse.success().setMsg("图片批量鉴黄成功");
    }

    @PutMapping("/check")
    public HttpResonse manualCheck(@Param("id") String id,
                                   @Param("result") int result){
        if (fileService.update(new UpdateWrapper<FileEntity>()
                .set("is_ban",result)
                .eq("file_id",id))) {
            return HttpResonse.success().setMsg("人工设置成功");
        }
        return HttpResonse.fail().setMsg("人工设置出错");
    }

    @PostMapping("/img")
    public HttpResonse updateNSFWScore(@Param("id") String id,
                                @Param("score") double score) {
        if (fileService.update(new UpdateWrapper<FileEntity>()
                .eq("file_id",fileService.getFileByU2Fid(id).getId())
                .set("nsfw_score",score))) {
            if (systemService.isImgCheckEnabled() && score > (systemService.getNSFWScore() / 100.0)) {
                if (fileService.update(new UpdateWrapper<FileEntity>()
                        .eq("file_id",fileService.getFileByU2Fid(id).getId())
                        .set("is_ban",NSFW_BAN))) {
                    return HttpResonse.success().setMsg("文件违规");
                }
            }
            return HttpResonse.fail().setMsg("设置成功");
        }
        return HttpResonse.fail().setMsg("设置失败");
    }

    @GetMapping("/img")
    public HttpResonse checkNSFWforImage(@Param("id") String id){
        Object object = null;
        try {
            object = nsfwService.checkImage(id);
        } catch (Exception e){
            e.printStackTrace();
        }
        return HttpResonse.success().setData(object);
    }

    @GetMapping("/video")
    public HttpResonse checkNSFWforVideo(@Param("id") String id){
        NSFWVO res = new NSFWVO();
        try {
            NSFWVO temp = nsfwService.checkVideo(id);
            res =  temp;
        } catch (Exception e){
            e.printStackTrace();
            return HttpResonse.fail().setMsg("出现错误");
        }
        if (res.getMaybeNSFW().size() > 0
                || res.getSuperNSFW().size() > 0) {
            return HttpResonse.success().setMsg("查询成功，可能为违规资源").setData(res);
        }
        return HttpResonse.success().setMsg("查询成功，为正常资源").setData(res);
    }
}
