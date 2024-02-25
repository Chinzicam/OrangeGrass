package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.FolderEntity;
import com.czc.Service.FolderService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Hanaue
 */
@RestController
@RequestMapping("/api/folder")
public class FolderController {

    @Autowired
    private FolderService folderService;


    @CostTime
    @GetMapping("/all")
    public List<FolderEntity> getAllFolders(){
        return folderService.list();
    }


    @CostTime
    @PutMapping("/rename")
    public HttpResonse renameFolder(@Param("folderId") String folderId,
                                  @Param("newName") String newName){
        if (folderService.renameFolder(folderId,newName)) {
            return HttpResonse.success().setMsg("重命名成功");
        }
        return HttpResonse.fail().setMsg("重命名失败");
    }


    @CostTime
    @PostMapping("")
    public HttpResonse addFolder(@RequestBody FolderEntity folder){
        Date date = new Date();
        folder.setFolderId(UUID.randomUUID().toString().trim().replaceAll("-", ""));
        folder.setUpdateTime(date);
        folder.setCreateTime(date);
        if (folderService.save(folder)) {
            return HttpResonse.success();
        }
        return HttpResonse.fail();
    }


    @CostTime
    @PostMapping("/{userId}/{folderId}")
    public HttpResonse addFolderForUser(@PathVariable("userId") String userId,
                                        @PathVariable("folderId") String folderId,
                                        @RequestBody FolderEntity folder){
        Date date = new Date();
        folder.setUpdateTime(date);
        folder.setCreateTime(date);
        folder.setFolderSize(0L);
        folder.setFolderId(UUID.randomUUID().toString().trim().replaceAll("-",""));
        return folderService.addFolderForUser(userId,folderId,folder);
    }


    @CostTime
    @DeleteMapping("/{folderId}/{userId}")
    public HttpResonse deleteFolder(@PathVariable("folderId") String folderId ,
                                    @PathVariable("userId") String userId){
        /**
         * 判断是否为本人的文件
         */
        if (!folderService.isHisFolder(userId,folderId)){
            return HttpResonse.fail().setMsg("你不能删除他人的文件夹");
        }

        /**
         * 判断是否为主文件夹
         */
        FolderEntity folder = folderService.getById(folderId);
        if (folder.getIsMain() == 1){
            return HttpResonse.fail().setMsg("主文件夹不可删除");
        }
        folderService.removeById(folderId);
        /**
         * 删除文件夹下文件
         */

        return HttpResonse.success().setMsg("删除成功");
    }

}
