package com.czc.Controller;

import cn.hutool.core.util.ArrayUtil;
import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.DTO.FileDto;
import com.czc.Entity.FileEntity;
import com.czc.Entity.User;
import com.czc.Service.*;
import com.czc.Util.SpeedLimiter;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.security.auth.Subject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

import static com.czc.Constant.FileConstant.UPLOAD_PATH;
import static com.czc.Constant.NSFW.NSFW_BAN;


/**
 * @author Hanaue
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private VoService voService;
    @Autowired
    private ChunkService chunkService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private UserService userService;

    @RequiresRoles("admin")
    @PutMapping("")
    public HttpResonse modifyFile(@RequestBody FileEntity file) {
        if (fileService.updateById(file)) {
            return HttpResonse.success().setMsg("修改文件成功");
        }
        return HttpResonse.fail().setMsg("修改文件失败");
    }

    @RequiresRoles("admin")
    @GetMapping("/checkedImages")
    public HttpResonse getCheckedImages(@Param("pageNum") Integer pageNum,
                                        @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询已鉴定图片成功")
                .setData(fileService.getCheckedImages(pageNum,pageSize));
    }

    @RequiresRoles("admin")
    @GetMapping("/uncheckedImages")
    public HttpResonse getUncheckedImages(@Param("pageNum") Integer pageNum,
                                          @Param("pageSize") Integer pageSize) {
        return HttpResonse.success().setMsg("查询未鉴定图片成功")
                .setData(fileService.getUncheckedImages(pageNum,pageSize));
    }


    @GetMapping("/images")
    public HttpResonse getImages() {
        return HttpResonse.success().setData(fileService.getImages());
    }

    @GetMapping("/check")
    public HttpResonse checkFile(@RequestParam("folderId") String folderId,
                                 @RequestParam("fileName") String fileName,
                                 @RequestParam("md5") String md5,
                                 @RequestParam("userId") String userId){
        //首先检查是否有完整的文件
        FileEntity file = new FileEntity();
        file.setMd5(md5);
        Boolean isUploaded = fileService.isExists(file);
        Map<String, Object> data = new HashMap<>();
        data.put("isUploaded",isUploaded);
        //如果有，就返回秒传
        if(isUploaded){
            fileService.addFileByMd5(folderId,userId,md5,fileName);
            return HttpResonse.success().setData(data);
        }

        //如果没有，就查找分片信息，并返回给前端
        List<Integer> chunkList = chunkService.selectChunkListByMd5(md5);
        data.put("chunkList",chunkList);

        return HttpResonse.success().setData(data);
    }


    @PostMapping("/upload/chunk")
    public HttpResonse uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                              @RequestParam("md5") String md5,
                              @RequestParam("index") Integer index,
                              @RequestParam("chunkTotal") Integer chunkTotal,
                              @RequestParam("fileSize") Long fileSize,
                              @RequestParam("fileName") String fileName,
                              @RequestParam("chunkSize") Long chunkSize,
                              @RequestParam("folderId") String folderId,
                              @RequestParam("userId") String userId,
                              @RequestParam("fileType") String fileType
    ){
        if (!systemService.isFileUploadEnabled()) {
            return HttpResonse.fail().setMsg("对不起，管理员关闭了文件上传功能");
        }
        System.out.println("上传【" + fileName + "】的第【" + index + "】/【" + chunkTotal + "】个分片");
        chunkService.saveChunk(chunk,md5,index,chunkSize,fileName,UPLOAD_PATH + fileType);
        if(Objects.equals(index, chunkTotal)){
            fileService.saveRecord(new FileDto(userId,folderId,chunk,new Date()),md5,fileSize,fileName,fileType);
            chunkService.deleteChunkByMd5(md5);
            return HttpResonse.success().setData(index);
        }else{
            return HttpResonse.success().setData(index);
        }
    }


    @CostTime
    @GetMapping("/s")
    public List<FileEntity> getAllFile(){
        return fileService.list();
    }

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/{userId}")
    public HttpResonse getUserFile(@PathVariable("userId") String userId) {
        return HttpResonse.success().setData(fileService.getUserFile(userId));
    }

    @CostTime
    @PutMapping("/rename")
    public HttpResonse renameFile(@Param("userFileId") String userFileId,
                                  @Param("newName") String newName){
        if (fileService.renameFile(userFileId,newName)) {
            return HttpResonse.success().setMsg("重命名成功");
        }
        return HttpResonse.fail().setMsg("重命名失败");
    }

    @PostMapping("/upload")
    public HttpResonse uploadFile(@Param("file") MultipartFile file ,
                                   @Param("userId") String userId,
                                  @Param("folderId") String folderId){
        /**
         * 确保文件夹属于该user
         */
        if (!folderService.isHisFolder(userId,folderId)){
            return HttpResonse.fail().setMsg("这不是你的文件夹");
        }
        /**
         * 确保该文件夹下没有该文件
         */
        if (!folderService.isExistsInThisFolder(folderId,file)){
            return HttpResonse.fail().setMsg("该文件【" + file.getOriginalFilename() + "】已存在于该目录");
        }

        /**
         * 上传
         */
        FileDto fileDto = new FileDto(userId,folderId,file,new Date());
        try {
            if(fileService.saveFile(fileDto)){
                return HttpResonse.success();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return HttpResonse.fail().setMsg("上传失败");
    }


    @CostTime
    @DeleteMapping("")
    public HttpResonse deleteFile(@Param("fileId") String fileId){
        if (voService.deleteUser2File(fileId)){
            return HttpResonse.success().setMsg("删除成功");
        }
        return HttpResonse.fail().setMsg("删除失败");
    }

    @GetMapping("/download/{u2fId}")
    public void downloadFile(@PathVariable("u2fId") String u2fId ,
                             HttpServletResponse response){
        fileService.downloadFile(u2fId,response);
    }

    @GetMapping("/download/recycle/{u2fId}")
    public void downloadRecycleFile(@PathVariable("u2fId") String u2fId,
                                    HttpServletResponse response){
        fileService.downloadRecycleFile(u2fId,response);
    }

    @GetMapping("/download/admin/{fileId}")
    public void downloadFileForAdmin(@PathVariable("fileId") String fileId,
                                     HttpServletResponse response) {
        fileService.downloadFileForAdmin(fileId,response);
    }

    @GetMapping("/chunkdownload/{u2fId}")
    public void chunkDownload(@PathVariable("u2fId") String u2fId ,
                              HttpServletResponse response ,
                              HttpServletRequest request){
        fileService.chunkDownload(u2fId,response,request);
    }

    @PostMapping("/chunkdownload/new")
    public void newDownload(@RequestParam("u2fId") String u2fId,
                         @RequestParam("fileName") String fileName,
                         @RequestParam("chunkSize") Integer chunkSize,
                         @RequestParam("chunkTotal") Integer chunkTotal,
                         @RequestParam("userId") String userId,
                         @RequestParam("index")Integer index,
                         HttpServletResponse response) {

        FileEntity file = fileService.getFileByU2Fid(u2fId);
        if (file.getIsBan() == NSFW_BAN) {
            return;
        }
        File resultFile = new File(file.getFileLocation());
        long offset = (long) chunkSize * (index - 1);
        if(Objects.equals(index, chunkTotal)){
            offset = resultFile.length() - chunkSize;
        }
        byte[] chunk = chunkService.getChunk(index, chunkSize, file.getFileLocation(),offset);
        System.out.println("下载文件分片" + file.getFileName() + "," + index + "," + chunkSize + "," + chunk.length+","+offset);
//        response.addHeader("Access-Control-Allow-Origin","Content-Disposition");
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Content-Length", "" + (chunk.length));
        response.setHeader("filename", fileName.replaceAll(",","-")
                .replaceAll(";","-"));
        response.setContentType("application/octet-stream");
        ServletOutputStream out = null;
        System.out.println(((User) SecurityUtils.getSubject().getPrincipal()));
        try {
            out = response.getOutputStream();
            /**
             * VIP不限速
             */
            if (userService.isMembership(userId)) {
                out.write(chunk);
            }
            /**
             * 普通用户根据设置限速
             */
            else {
                /**
                 * 全局下载限速
                 */
                if (systemService.isSpeedLimitEnabled()) {
                    int speedLimitRate = Math.toIntExact(systemService.getSpeedLimitRate());
                    byte[][] toSend = ArrayUtil.split(chunk,speedLimitRate * 1024);
                    SpeedLimiter speedLimiter = new SpeedLimiter(speedLimitRate);
                    for (int i = 0 ; i < toSend.length ; ++i){
                        out.write(toSend[i]);
                        speedLimiter.delayNextBytes(toSend[i].length);
                    }
                } else {
                    out.write(chunk);
                }
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
