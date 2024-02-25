package com.czc.Service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czc.Entity.FileEntity;
import com.czc.Entity.ThumbnailEntity;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Mapper.ThumbnailMapper;
import com.czc.Service.FileService;
import com.czc.Service.ThumbnailService;
import com.czc.Service.VoService;
import com.czc.Util.FileUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

import static com.czc.Constant.FileConstant.UPLOAD_PATH;
import static com.czc.Constant.MinetypeConstant.MINETYPE_IMAGE;
import static com.czc.Constant.MinetypeConstant.MINETYPE_VIDEO;
import static com.czc.Constant.NSFW.NSFW_BAN;
import static com.czc.Constant.NSFW.NSFW_IMG_PATH;

/**
 * @author Hanaue
 */
@Service
public class ThumbnailServiceImpl extends ServiceImpl<ThumbnailMapper, ThumbnailEntity> implements ThumbnailService {

    @Autowired
    private FileService fileService;
    @Autowired
    private ThumbnailMapper thumbnailMapper;
    @Autowired
    private VoService voService;

    /**
     * 下载缩略图
     * @param picId
     * @param response
     */
    @Override
    public void downloadThumbnail(String picId, HttpServletResponse response){

        User2FileDTO u2fvo = voService.selectUser2FileDTOById(picId);
        FileEntity file = fileService.getById(u2fvo == null ? picId : u2fvo.getFileId());
        /**
         * 如果文件违规
         */
        if (file.getIsBan() == NSFW_BAN) {
            FileEntity banedFile = new FileEntity();
            banedFile.setFileName("banned.jpeg");
            banedFile.setFileLocation(NSFW_IMG_PATH);
            FileUtil.download(response,banedFile);
            return;
        }
        String fileName = file.getFileName();
        String extension = fileName.substring(fileName.lastIndexOf('.')+1,fileName.length());
        if (!MINETYPE_IMAGE.contains(extension.toLowerCase()) && !MINETYPE_VIDEO.contains(extension.toLowerCase())) {
            return;
        }
        if (MINETYPE_IMAGE.contains(extension.toLowerCase())){
            ThumbnailEntity thumbnail = fileService.getThumbnailByPicId(file.getId());
            /**
             * 如果缩略图不存在，先生成再发送
             */
            if (thumbnail == null) {
                System.out.println("缩略图不存在，即将生成");
                thumbnail = new ThumbnailEntity();
                String uuid = UUID.randomUUID().toString().trim().replaceAll("-","");
                thumbnail.setId(uuid);
                try {
                    System.out.println("生成中");
                    Thumbnails.of(new File(file.getFileLocation()))
                            .size(200,200)
                            .toFile(new File(getThumbnailPath(thumbnail)));
                    System.out.println("生成完成");
                    thumbnail.setFileId(file.getId());
                    thumbnail.setName(uuid+".png");
                    thumbnailMapper.insert(thumbnail);
                    FileEntity downloadFile = new FileEntity();
                    downloadFile.setFileName(thumbnail.getName());
                    downloadFile.setFileLocation(getThumbnailPath(thumbnail));
                    System.out.println("发送");
                    FileUtil.download(response,downloadFile);
                    return;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            /**
             * 如果缩略图存在，直接向前端发送
             */
            FileEntity downloadFile = new FileEntity();
            downloadFile.setFileName(thumbnail.getName());
            downloadFile.setFileLocation(getThumbnailPath(thumbnail));
            FileUtil.download(response,downloadFile);
            return;
        }
        if (MINETYPE_VIDEO.contains(extension.toLowerCase())) {
            ThumbnailEntity thumbnail = fileService.getThumbnailByPicId(file.getId());
            /**
             * 如果缩略图不存在，先生成再发送
             */
            if (thumbnail == null) {
                thumbnail = new ThumbnailEntity();
                String uuid = UUID.randomUUID().toString().trim().replaceAll("-","");
                thumbnail.setId(uuid);
                generateThumbnailForVideo(thumbnail, file, uuid, response);
                return;
            }
            /**
             * 如果缩略图存在，直接向前端发送
             */
            FileEntity downloadFile = new FileEntity();
            downloadFile.setFileName(thumbnail.getName());
            downloadFile.setFileLocation(getThumbnailPath(thumbnail));
            FileUtil.download(response,downloadFile);
        }
    }

    @Override
    public void downloadThumbnailForAdmin(String picId, HttpServletResponse response){

        User2FileDTO u2fvo = voService.selectUser2FileDTOById(picId);
        FileEntity file = fileService.getById(u2fvo == null ? picId : u2fvo.getFileId());
        String fileName = file.getFileName();
        String extension = fileName.substring(fileName.lastIndexOf('.')+1,fileName.length());
        if (!MINETYPE_IMAGE.contains(extension.toLowerCase()) && !MINETYPE_VIDEO.contains(extension.toLowerCase())) {
            return;
        }
        if (MINETYPE_IMAGE.contains(extension.toLowerCase())){
            ThumbnailEntity thumbnail = fileService.getThumbnailByPicId(file.getId());
            /**
             * 如果缩略图不存在，先生成再发送
             */
            if (thumbnail == null) {
                System.out.println("缩略图不存在，即将生成");
                thumbnail = new ThumbnailEntity();
                String uuid = UUID.randomUUID().toString().trim().replaceAll("-","");
                thumbnail.setId(uuid);
                try {
                    System.out.println("生成中");
                    Thumbnails.of(new File(file.getFileLocation()))
                            .size(200,200)
                            .toFile(new File(getThumbnailPath(thumbnail)));
                    System.out.println("生成完成");
                    thumbnail.setFileId(file.getId());
                    thumbnail.setName(uuid+".png");
                    thumbnailMapper.insert(thumbnail);
                    FileEntity downloadFile = new FileEntity();
                    downloadFile.setFileName(thumbnail.getName());
                    downloadFile.setFileLocation(getThumbnailPath(thumbnail));
                    System.out.println("发送");
                    FileUtil.download(response,downloadFile);
                    return;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            /**
             * 如果缩略图存在，直接向前端发送
             */
            FileEntity downloadFile = new FileEntity();
            downloadFile.setFileName(thumbnail.getName());
            downloadFile.setFileLocation(getThumbnailPath(thumbnail));
            FileUtil.download(response,downloadFile);
            return;
        }
        if (MINETYPE_VIDEO.contains(extension.toLowerCase())) {
            ThumbnailEntity thumbnail = fileService.getThumbnailByPicId(file.getId());
            /**
             * 如果缩略图不存在，先生成再发送
             */
            if (thumbnail == null) {
                thumbnail = new ThumbnailEntity();
                String uuid = UUID.randomUUID().toString().trim().replaceAll("-","");
                thumbnail.setId(uuid);
                generateThumbnailForVideo(thumbnail, file, uuid, response);
                return;
            }
            /**
             * 如果缩略图存在，直接向前端发送
             */
            FileEntity downloadFile = new FileEntity();
            downloadFile.setFileName(thumbnail.getName());
            downloadFile.setFileLocation(getThumbnailPath(thumbnail));
            FileUtil.download(response,downloadFile);
        }
    }


    public void generateThumbnailForVideo(ThumbnailEntity thumbnail, FileEntity file, String uuid, HttpServletResponse response) {
        //最后获取到的视频的图片的路径
        String videPicture="";
        //Frame对象
        Frame frame = null;
        //标识
        int flag = 0;
        try {
             /*
            获取视频文件
            */
            // FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(videoPath + "/" + videoFileName);
            FFmpegFrameGrabber fFmpegFrameGrabber = new FFmpegFrameGrabber(file.getFileLocation());
            fFmpegFrameGrabber.start();

            //获取视频总帧数
            int ftp = fFmpegFrameGrabber.getLengthInFrames();
            System.out.println("时长 " + ftp / fFmpegFrameGrabber.getFrameRate() / 60);


            while (flag <= ftp) {
                frame = fFmpegFrameGrabber.grabImage();

                /*
                对视频的第五帧进行处理
                 */
                if (frame != null && flag==5) {

//                    //文件转换
                    BufferedImage bufferedImage = FrameToBufferedImage(frame);
                    try {
                        System.out.println("生成中");
                        Thumbnails.of(bufferedImage)
                                .size(200,200)
                                .toFile(new File(getThumbnailPath(thumbnail)));
                        System.out.println("生成完成");
                        thumbnail.setFileId(file.getId());
                        thumbnail.setName(uuid+".png");
                        thumbnailMapper.insert(thumbnail);
                        FileEntity downloadFile = new FileEntity();
                        downloadFile.setFileName(thumbnail.getName());
                        downloadFile.setFileLocation(getThumbnailPath(thumbnail));
                        System.out.println("发送");
                        FileUtil.download(response,downloadFile);
                        return;
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                flag++;
            }
            fFmpegFrameGrabber.stop();
            fFmpegFrameGrabber.close();
        } catch (Exception E) {
            E.printStackTrace();
        }
        return ;
    }

    public BufferedImage FrameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }

    private String getThumbnailPath(ThumbnailEntity thumbnail){
        return UPLOAD_PATH + "thumbnail/"+thumbnail.getId()+".png";
//        return "/home/spring/netdisk/upload/thumbnail/"+thumbnail.getId()+".png";
//        return "backend/upload/thumbnail/"+thumbnail.getId()+".png";
//        return "upload/thumbnail/"+thumbnail.getId()+".png";
    }
}
