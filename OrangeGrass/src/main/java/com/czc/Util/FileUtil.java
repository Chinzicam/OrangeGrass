package com.czc.Util;

import com.czc.Entity.FileEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class FileUtil {

    /**
     * 文件支持分块下载和断点续传
     */
    public static void chunkDownload(String filename, String fileUrl, Long size, String contentType ,HttpServletResponse response,HttpServletRequest request) {
        String range = request.getHeader("Range");
        //开始下载位置
        long startByte = 0;
        //结束下载位置
        long endByte = size - 1;
        log.debug("文件[{}]开始位置：{}，文件结束位置：{}，文件总长度：{}", filename, startByte, endByte, size);

        //有range的话
        if (range != null && range.contains("bytes=") && range.contains("-")) {
            range = range.substring(range.lastIndexOf("=") + 1).trim();
            String[] ranges = range.split("-");
            try {
                //判断range的类型
                if (ranges.length == 1) {
                    //类型一：bytes=-2343
                    if (range.startsWith("-")) {
                        endByte = Long.parseLong(ranges[0]);
                    }
                    //类型二：bytes=2343-
                    else if (range.endsWith("-")) {
                        startByte = Long.parseLong(ranges[0]);
                    }
                }
                //类型三：bytes=22-2343
                else if (ranges.length == 2) {
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }
                //http状态码要为206：表示获取部分内容
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = size - 1;
                log.error("Range Occur Error,Message:{}", e.getLocalizedMessage());
            }
        }

        //要下载的长度
        long contentLength = endByte - startByte + 1;

        //各种响应头设置
        //支持断点续传，获取部分字节内容：
        response.setHeader("Accept-Ranges", "bytes");
        response.setContentType(contentType);
        //inline表示浏览器直接使用，attachment表示下载，fileName表示下载的文件名, 解决下载文件时文件名乱码问题
        String contentDisposition = String.format("inline;filename=%s",
                new String(filename.replaceAll(",","-")
                                   .replaceAll(";","-")
                                   .getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        response.setHeader("Content-Disposition", contentDisposition);
        response.setContentLengthLong(contentLength);
        // Content-Range，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + size);

        try (
                InputStream inputStream = new FileInputStream(fileUrl);
                BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        ) {
            StreamUtils.copyRange(inputStream, outputStream, startByte, endByte);
            log.info(String.format("下载[ %s ]完毕：%d-%d",  filename, startByte, endByte));
        } catch (ClientAbortException e) {
            log.warn(String.format("停止[ %s ]下载：%d-%d",  filename, startByte, endByte));
            //捕获此异常表示拥护停止下载
        } catch (IOException e) {
            log.info(String.format("用户[%s]下载[ %s ]IO异常，Message：{}", filename, e.getLocalizedMessage()));
        }
    }

    public static void download(HttpServletResponse response, FileEntity fileEntity) {
        String downloadUrl = fileEntity.getFileLocation();
        if (Objects.isNull(downloadUrl)) {
            // 如果接收参数为空则抛出异常，由全局异常处理类去处理。
            throw new NullPointerException("下载地址为空");
        }
        // 读文件
        File file = new File(downloadUrl);
        if (!file.exists()) {
            System.out.println("下载文件的地址不存在");
            // 如果不存在则抛出异常，由全局异常处理类去处理。
        }
        // 获取用户名
        String fileName = fileEntity.getFileName();
        // 重置response
        response.reset();
        // ContentType，即告诉客户端所发送的数据属于什么类型
        response.setContentType("application/octet-stream; charset=UTF-8");
        // 获得文件的长度
        response.setHeader("Content-Length", String.valueOf(file.length()));
        // 设置编码格式
        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName.replaceAll(",","-")
                .replaceAll(";","-"));
        //设置跨域
        response.setHeader("Access-Control-Allow-Origin","*");
        // 发送给客户端的数据
        try {
            OutputStream outputStream = response.getOutputStream();
            byte[] buff = new byte[1024];
            BufferedInputStream bis = null;
            // 读取文件
            bis = new BufferedInputStream(new FileInputStream(new File(downloadUrl)));
            int i = bis.read(buff);
            // 只要能读到，则一直读取
            while (i != -1) {
                // 将文件写出
                outputStream.write(buff, 0, buff.length);
                // 刷出
                outputStream.flush();
                i = bis.read(buff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param file
     * @param targetDirPath 存储MultipartFile文件的目标文件夹
     * @return 文件的存储的绝对路径
     */
    public static String saveMultipartFile(MultipartFile file, String targetDirPath,String uuid){

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            return null;
        } else {

            /*获取文件原名称*/
            String originalFilename = file.getOriginalFilename();
            /*获取文件格式*/
            String fileFormat = originalFilename.substring(originalFilename.lastIndexOf("."));
            toFile = new File(targetDirPath + File.separator + uuid + fileFormat);
//            toFile = new File(targetDirPath + File.separator + originalFilename);

            String absolutePath = null;
            try {
                absolutePath = toFile.getCanonicalPath();
                System.out.println(absolutePath);
                /*判断路径中的文件夹是否存在，如果不存在，先创建文件夹*/
                String dirPath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                InputStream ins = file.getInputStream();

                inputStreamToFile(ins, toFile);
                ins.close();

            } catch (IOException e) {
                e.printStackTrace();
            }



            return absolutePath;
        }

    }

    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地临时文件
     *
     * @param file
     */
    public static void deleteTempFile(File file) {
        if (file != null) {
            File del = new File(file.toURI());
            del.delete();
        }
    }

}
