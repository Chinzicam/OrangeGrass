package com.czc.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czc.Entity.AvatarEntity;
import com.czc.Entity.FileEntity;
import com.czc.Mapper.AvatarMapper;
import com.czc.Service.AvatarService;
import com.czc.Util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

import static com.czc.Constant.FileConstant.UPLOAD_PATH;
import static com.czc.Constant.MinetypeConstant.MINETYPE_IMAGE;

@Service
public class AvatarServiceImpl extends ServiceImpl<AvatarMapper,AvatarEntity> implements AvatarService {

    @Autowired
    private AvatarMapper avatarMapper;

    @Override
    public void sendRecentAvatar(String userId, HttpServletResponse response) {
        AvatarEntity avatar = avatarMapper.selectRecentAvatar(userId);
        if (avatar == null) {
            return;
        }
        FileEntity file = new FileEntity();
        file.setFileLocation(avatar.getAvatarLocation());
        file.setFileName(avatar.getAvatarLocation().substring(avatar.getAvatarLocation().lastIndexOf('/')+1));
        FileUtil.download(response,file);
    }

    @Override
    public boolean setAvatar(String userId, MultipartFile file) {
        /*获取文件原名称*/
        String originalFilename = file.getOriginalFilename();
        /*获取文件格式*/
        String fileFormat = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!MINETYPE_IMAGE.contains(fileFormat.toLowerCase().substring(1))) {
            System.out.println(fileFormat.toLowerCase() + "不是图片");
            return false;
        }
        String avatarId = UUID.randomUUID().toString().replaceAll("-","");
        FileUtil.saveMultipartFile(file,UPLOAD_PATH + "avatar/" , avatarId);
        AvatarEntity avatar = new AvatarEntity();
        avatar.setAvatarId(avatarId);
        avatar.setUserId(userId);
        avatar.setAvatarLocation(UPLOAD_PATH + "avatar/" + avatarId + fileFormat);
        avatar.setCreateTime(new Date());
        save(avatar);
        return true;
    }
}
