package com.czc.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.FileEntity;
import com.czc.Entity.VO.NSFWVO;
import com.czc.Service.FileService;
import com.czc.Service.NSFWService;
import com.czc.Service.SystemService;
import com.czc.Service.VoService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static com.czc.Constant.NSFW.NSFW_BAN;


@Service
public class NSFWServiceImpl implements NSFWService {

    @Autowired
    private FileService fileService;
    @Autowired
    private VoService voService;
    @Autowired
    private SystemService systemService;

    @Override
    public Object checkImages(List<FileEntity> files) throws Exception {
        double banScore = systemService.getNSFWScore();
        List<String> paths = new ArrayList<>();
        for (FileEntity file : files) {
            File tmp = new File(file.getFileLocation());
            paths.add(tmp.getAbsolutePath().replaceAll("\\\\","/"));
        }
        String url = "http://127.0.0.1:5000/images";
        // 1. 创建HttpClient对象
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(1000000000))
                .setResponseTimeout(Timeout.ofSeconds(1000000000))
                .setConnectTimeout(Timeout.ofSeconds(1000000000))
                .build();
        // 1. 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        // 2. 创建HttpGet对象
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("paths",
                JSON.toJSONString(paths)
                ));
        UrlEncodedFormEntity form = new UrlEncodedFormEntity(paramList,StandardCharsets.UTF_8);
        httpPost.setEntity(form);
        CloseableHttpResponse response = null;
        try {
            // 3. 执行GET请求
            response = httpClient.execute(httpPost);
//            System.out.println(response.getStatusLine());
            // 4. 获取响应实体
            HttpEntity entity = response.getEntity();
            // 5. 处理响应实体
            if (entity != null) {
                System.out.println("长度：" + entity.getContentLength());
                String content = EntityUtils.toString(entity);
                System.out.println("内容：" + content);
                List<Object> res = (List<Object>) JSON.parse(content);
                for (int i = 0 ; i < files.size() ; ++i) {
                    FileEntity file = files.get(i);
                    file.setNsfwScore(Double.parseDouble(res.get(i).toString()));
                    if (file.getNsfwScore() >= banScore) {
                        file.setIsBan(NSFW_BAN);
                    }
//                    System.out.println(file);
                    fileService.updateById(file);
                }
                return content;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    /**
     * 判断该id的视频是否违规
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public NSFWVO checkVideo(String id) throws Exception{
        if ("".equals(id) || id == null){
            return new NSFWVO();
        }
        FileEntity file = fileService.getFileByU2Fid(id);
        File temp = new File(file.getFileLocation());
        String filePath = temp.getAbsolutePath().replaceAll("\\\\","/");
        String url = "http://127.0.0.1:5000/video/" + new String(Base64.getEncoder().encode(filePath.getBytes(StandardCharsets.UTF_8)));
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(1000000000))
                .setResponseTimeout(Timeout.ofSeconds(1000000000))
                .setConnectTimeout(Timeout.ofSeconds(1000000000))
                .build();
        // 1. 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        // 2. 创建HttpGet对象
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 3. 执行GET请求
            response = httpClient.execute(httpGet);
//            System.out.println(response.getStatusLine());
            // 4. 获取响应实体
            HttpEntity entity = response.getEntity();
            // 5. 处理响应实体
            if (entity != null) {
                String content = EntityUtils.toString(entity);
                return JSON.parseObject(content,NSFWVO.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new NSFWVO();
    }


    /**
     * 判断该id的图片是否违规
     * @param id
     * @return
     */
    @Override
    public Object checkImage(String id) throws Exception{
        if ("".equals(id) || id == null){
            return 0;
        }
        FileEntity file = fileService.getFileByU2Fid(id);
        if (file == null) {
            file = fileService.getById(id);
        }
        File temp = new File(file.getFileLocation());
        String filePath = temp.getAbsolutePath();
        String url = "http://127.0.0.1:5000/image";
        // 1. 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        // 2. 创建HttpGet对象
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> paramList = new ArrayList<>();
        paramList.add(new BasicNameValuePair("id",
                id
        ));
        paramList.add(new BasicNameValuePair("path",
                filePath
        ));
        UrlEncodedFormEntity form = new UrlEncodedFormEntity(paramList,StandardCharsets.UTF_8);
        httpPost.setEntity(form);
        CloseableHttpResponse response = null;
        try {
            // 3. 执行POST请求
            response = httpClient.execute(httpPost);
//            System.out.println(response.getStatusLine());
            // 4. 获取响应实体
            HttpEntity entity = response.getEntity();
            // 5. 处理响应实体
            if (entity != null) {
                System.out.println("长度：" + entity.getContentLength());
                String content = EntityUtils.toString(entity);
                System.out.println("内容：" + content);
                return content;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 6. 释放资源
            try {
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }
}
