package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Entity.Advertisement;
import org.bytedeco.javacv.Frame;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

public interface AdvertiseService extends IService<Advertisement> {
    public boolean addAdvertisement(String name, String description, MultipartFile file);
    public void downloadThumbnail(String advertisementId, HttpServletResponse response);
    public void chunkDownload(String u2fid , HttpServletResponse response, HttpServletRequest request);

}
