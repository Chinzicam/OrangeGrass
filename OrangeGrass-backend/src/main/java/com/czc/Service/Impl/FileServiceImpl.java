package com.czc.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.czc.Entity.DTO.FileDto;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ThumbnailEntity;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Mapper.FileMapper;
import com.czc.Service.*;
import com.czc.Util.FileUtil;
import com.czc.Util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.czc.Constant.FileConstant.UPLOAD_PATH;
import static com.czc.Constant.MinetypeConstant.*;
import static com.czc.Constant.NSFW.*;


@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private FolderService folderService;
    @Autowired
    private VoService voService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private NSFWService nsfwService;

    /**
     * corePoolSize线程池的核心线程数
     * maximumPoolSize能容纳的最大线程数
     * keepAliveTime空闲线程存活时间
     * unit 存活的时间单位
     * workQueue 存放提交但未执行任务的队列
     * threadFactory 创建线程的工厂类
     * handler 等待队列满后的拒绝策略
     */
//    ExecutorService threadPool = new ThreadPoolExecutor(2,5,
//            1L, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<>(3),
//            Executors.defaultThreadFactory(),
//            new ThreadPoolExecutor.AbortPolicy());

    @Override
    public List<FileEntity> getImages() {
        return list(new QueryWrapper<FileEntity>()
                .like("minetype","image%"));
    }

    @Override
    public PageInfo<FileEntity> getCheckedImages(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<FileEntity> list = fileMapper.selectCheckedImages();
        return new PageInfo<FileEntity>(list);
    }

    @Override
    public PageInfo<FileEntity> getUncheckedImages(Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<FileEntity> list = fileMapper.selectUncheckedImages();
        return new PageInfo<FileEntity>(list);
    }

    @Override
    public List<FileEntity> getUncheckedImages(){
        return fileMapper.selectUncheckedImages();
    }

    /**
     *
     */
    @Override
    public boolean addFileByMd5(String folderId, String userId, String md5, String fileName) {
        FileEntity file = fileMapper.selectByMd5(md5);
        if (file != null) {
            User2FileDTO u2f = new User2FileDTO();
            u2f.setFileId(file.getId());
            u2f.setFileName(fileName);
            u2f.setUserId(userId);
            u2f.setId(UUID.randomUUID().toString().replaceAll("-",""));
            Folder2XDTO f2x = new Folder2XDTO();
            f2x.setXType("FILE");
            f2x.setFolderId(folderId);
            f2x.setXId(u2f.getId());
            f2x.setId(UUID.randomUUID().toString().replaceAll("-",""));
            return voService.addU2F(u2f) && folderService.addFile(f2x);
        }
        return false;
    }


    /**
     *
     * @param userId
     * @return
     */
    @Override
    public List<FileEntity> getUserFile(String userId) {
        return fileMapper.selectByUserId(userId);
    }

    /**
     *
     * @param md5
     * @return
     */
    @Override
    public FileEntity getByMD5(String md5){
        return fileMapper.selectByMd5(md5);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public FileEntity getFileByU2Fid(String id){
        User2FileDTO u2f = voService.selectUser2FileDTOById(id);
        FileEntity file = getById(u2f.getFileId());
        return file;
    }

    /**
     * 获取文件夹下所有文件
     * @param folderId
     * @return
     */
    @Override
    public List<FileEntity> selectFilesByFolderId(String folderId){
        List<FileEntity> files = fileMapper.selectByFolderId(folderId);
        return files;
    }

    /**
     * 重命名文件
     * @param userFileId
     * @param newName
     * @return
     */
    @Override
    public boolean renameFile(String userFileId,String newName){
        User2FileDTO u2fvo = voService.selectUser2FileDTOById(userFileId);
        FileEntity file = fileMapper.selectById(u2fvo.getFileId());
        if (file == null || u2fvo == null){
            return false;
        }
        String extension = file.getFileName().substring(file.getFileName().lastIndexOf('.'));
        file.setUpdateTime(new Date());
        u2fvo.setFileName(newName + extension);
        if (voService.updateU2FDTO(u2fvo) && fileMapper.updateById(file) > 0){
            return true;
        }
        return false;
    }

    /**
     * 分片上传时由此方法向数据库插入数据
     * @param file
     * @param md5
     * @return
     */
    @Override
    public boolean saveRecord(FileDto file, String md5, Long fileSize, String fileName, String type) {
        String path = UPLOAD_PATH + type;
        FileEntity fileEntity = new FileEntity();
        fileEntity.setMd5(md5);
        Date date = new Date();
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        /**
         * 向数据库【文件表】插入记录
         */
        fileEntity.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
        fileEntity.setFileName(fileName);
        fileEntity.setMinetype(type);
        fileEntity.setCreateTime(date);
        fileEntity.setUpdateTime(date);
        fileEntity.setFileSize(fileSize);
        // 旧的文件或目录
        File oldName = new File(path + "/" + fileName);
        // 新的文件或目录
        File newName = new File(path + "/" + uuid + fileName.substring(fileName.lastIndexOf('.'),fileName.length()));
        if (newName.exists()) {  //  确保新的文件名不存在
            System.out.println("文件不存在");
        }
        if(oldName.renameTo(newName)) {
            System.out.println("已重命名");
        } else {
            System.out.println("Error");
        }
        fileEntity.setFileLocation(path + "/" + uuid + fileName.substring(fileName.lastIndexOf('.'),fileName.length()));
        fileMapper.insert(fileEntity);
        /**
         * 向用户文件表插入数据
         */
        User2FileDTO ufvo = new User2FileDTO();
        ufvo.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
        ufvo.setUserId(file.getUserId());
        ufvo.setFileId(fileEntity.getId());
        ufvo.setFileName(fileName);
        userService.addFile(ufvo);

        /**
         * 如果已开启图片上传鉴黄
         */
        //原版
//        if (systemService.isImgCheckEnabled()) {
//            double banScore = systemService.getNSFWScore() / 100.0;
//            try {
//                double score = nsfwService.checkImage(ufvo.getId());
//                if (score > banScore) {
//                    update(new UpdateWrapper<FileEntity>()
//                            .set("nsfw_score",score)
//                            .set("is_ban",NSFW_BAN)
//                            .eq("file_id",fileEntity.getId()));
//                } else {
//                    update(new UpdateWrapper<FileEntity>()
//                            .set("nsfw_score",score)
//                            .eq("file_id",fileEntity.getId()));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        //新版
        if (systemService.isImgCheckEnabled()) {
            if (MINETYPE_IMAGE.contains(fileName.substring(fileName.lastIndexOf('.')+1).toLowerCase())) {
                try {
                    nsfwService.checkImage(ufvo.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 向文件夹-文件表插入数据
         */
        Folder2XDTO fxvo = new Folder2XDTO();
        fxvo.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
        fxvo.setFolderId(file.getFolderId());
        fxvo.setXId(ufvo.getId());
        fxvo.setXType("FILE");
        folderService.addFile(fxvo);

        return true;
    }

    /**
     * 将前端上传的文件存储到磁盘
     * @param file
     * @return
     */
    @Override
    public boolean saveFile(FileDto file) throws Exception{
        String fileName = file.getFile().getOriginalFilename();
        String type = file.getFile().getContentType();
        String path = UPLOAD_PATH + type;
        String md5 = MD5Util.calcMD5(file.getFile().getInputStream());
        FileEntity fileEntity = new FileEntity();
        fileEntity.setMd5(md5);
        Date date = new Date();
        /**
         * 文件不存在时
         */
        if (!isExists(fileEntity)) {
            /**
             * 存储文件到磁盘
             */
            String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
            FileUtil.saveMultipartFile(file.getFile(),path,uuid);
            /**
             * 向数据库【文件表】插入记录
             */
            fileEntity.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
            fileEntity.setFileName(fileName);
            fileEntity.setMinetype(type);
            fileEntity.setCreateTime(date);
            fileEntity.setUpdateTime(date);
            fileEntity.setFileSize(file.getFile().getSize());
            fileEntity.setFileLocation(path + "/" + uuid + fileName.substring(fileName.lastIndexOf('.'),fileName.length()));
            fileMapper.insert(fileEntity);
            /**
             * 向用户文件表插入数据
             */
            User2FileDTO ufvo = new User2FileDTO();
            ufvo.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
            ufvo.setUserId(file.getUserId());
            ufvo.setFileId(fileEntity.getId());
            ufvo.setFileName(file.getFile().getOriginalFilename());
            userService.addFile(ufvo);
            /**
             * 向文件夹-文件表插入数据
             */
            Folder2XDTO fxvo = new Folder2XDTO();
            fxvo.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
            fxvo.setFolderId(file.getFolderId());
            fxvo.setXId(ufvo.getId());
            fxvo.setXType("FILE");
            folderService.addFile(fxvo);

            return true;
        }

        /**
         * 文件存在时
         */
        fileEntity = fileMapper.selectByMd5(md5);

        /**
         * 向用户文件表插入数据
         */
        User2FileDTO ufvo = new User2FileDTO();
        ufvo.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
        ufvo.setUserId(file.getUserId());
        ufvo.setFileId(fileEntity.getId());
        ufvo.setFileName(file.getFile().getOriginalFilename());
        userService.addFile(ufvo);
        /**
         * 向文件夹-文件表插入数据
         */
        Folder2XDTO fxvo = new Folder2XDTO();
        fxvo.setId(UUID.randomUUID().toString().trim().replaceAll("-",""));
        fxvo.setFolderId(file.getFolderId());
        fxvo.setXId(ufvo.getId());
        fxvo.setXType("FILE");
        folderService.addFile(fxvo);

        return true;
    }

    @Override
    public void chunkDownload(String u2fid , HttpServletResponse response, HttpServletRequest request){
        User2FileDTO u2fvo = voService.selectUser2FileDTOById(u2fid);
        FileEntity file = fileMapper.selectById(u2fvo.getFileId());
        /**
         * 文件违规
         */
        if (file.getIsBan() == NSFW_BAN) {
            System.out.println("文件违规");
            FileEntity banedFile = new FileEntity();
            banedFile.setFileName("banned.jpeg");
            banedFile.setFileLocation(NSFW_IMG_PATH);
            FileUtil.download(response,banedFile);
            return;
        }
        FileUtil.chunkDownload(file.getFileName(),file.getFileLocation(),file.getFileSize(),file.getMinetype(),response,request);
    }


    @Override
    public void downloadRecycleFile(String u2fId, HttpServletResponse response) {
        User2FileDTO u2fvo = voService.getDeletedU2F(u2fId);
        FileEntity fileEntity = fileMapper.selectById(u2fvo.getFileId());
        fileEntity.setFileName(u2fvo.getFileName());
        /**
         * 文件违规
         */
        if (fileEntity.getIsBan() == NSFW_BAN) {
            FileEntity banedFile = new FileEntity();
            banedFile.setFileName("banned.jpeg");
            banedFile.setFileLocation(NSFW_IMG_PATH);
            FileUtil.download(response,banedFile);
            return;
        }
        /**
         * 下载
         */
        FileUtil.download(response,fileEntity);
    }

    /**
     * 下载文件
     * @param u2fId
     * @param response
     * @return
     */
    @Override
    public void downloadFile(String u2fId, HttpServletResponse response) {
        User2FileDTO u2fvo = voService.selectUser2FileDTOById(u2fId);
        FileEntity fileEntity = fileMapper.selectById(u2fvo.getFileId());
        fileEntity.setFileName(u2fvo.getFileName());
        /**
         * 文件违规
         */
        if (fileEntity.getIsBan() == NSFW_BAN) {
            FileEntity banedFile = new FileEntity();
            banedFile.setFileName("banned.jpeg");
            banedFile.setFileLocation(NSFW_IMG_PATH);
            FileUtil.download(response,banedFile);
            return;
        }
        /**
         * 下载
         */
        FileUtil.download(response,fileEntity);
    }

    @Override
    public void downloadFileForAdmin(String fileId, HttpServletResponse response) {
        FileEntity fileEntity = fileMapper.selectById(fileId);
        if (fileEntity == null) {
            User2FileDTO u2f = voService.selectUser2FileDTOById(fileId);
            fileEntity = new FileEntity();
            fileEntity = fileMapper.selectById(u2f.getFileId());
            fileEntity.setFileName(u2f.getFileName());
        }
        /**
         * 下载
         */
        FileUtil.download(response,fileEntity);
    }


    /**
     * 根据userId查找他的文件
     * @param userId
     * @return
     */
    @Override
    public List<FileEntity> selectFilesByUserId(String userId){
        List<FileEntity> files = fileMapper.selectByUserId(userId);
        for (FileEntity file : files){
            if (file.getFileName() == null || "".equals(file.getFileName())){
                file.setFileName(fileMapper.selectById(file.getId()).getFileName());
                User2FileDTO vo = new User2FileDTO();
                vo.setFileName(file.getFileName());
                vo.setUserId(userId);
                vo.setFileId(file.getId());
                voService.updateU2FDTOFileName(vo);
            }
        }
        return files;
    }


    /**
     * 根据文件后缀获取文件类型
     * @param s
     * @return
     */
    public String getMimetype(String s){
        if (MINETYPE_IMAGE.contains(s.toLowerCase())){
            return "image";
        }
        if (MINETYPE_DOCUMENT.contains(s.toLowerCase())){
            return "document";
        }
        if (MINETYPE_EXE.contains(s.toLowerCase())){
            return "application";
        }
        if (MINETYPE_MUSIC.contains(s.toLowerCase())){
            return "music";
        }
        if (MINETYPE_VIDEO.contains(s.toLowerCase())){
            return "video";
        }
        return "other";
    }

    /**
     * 根据图片id获取到缩略图实体
     * @param id
     * @return
     */
    @Override
    public ThumbnailEntity getThumbnailByPicId(String id){
        return fileMapper.selectThumbnailByPicId(id);
    }

    /**
     *根据md5判断文件是否存在
     */
    @Override
    public boolean isExists(FileEntity fileEntity){
        String md5 = fileEntity.getMd5();
        FileEntity file = fileMapper.selectByMd5(md5);
        if (file == null){
            return false;
        }
        return true;
    }
}
