package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Entity.AvatarEntity;
import org.bytedeco.opencv.presets.opencv_core;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface AvatarService extends IService<AvatarEntity> {

    public boolean setAvatar(String userId, MultipartFile file);
    public void sendRecentAvatar(String userId, HttpServletResponse response);
}
