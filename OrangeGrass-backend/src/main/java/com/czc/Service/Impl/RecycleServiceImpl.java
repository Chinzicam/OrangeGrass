package com.czc.Service.Impl;

import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.FileEntity;
import com.czc.Mapper.RecycleMapper;
import com.czc.Service.RecycleService;
import com.czc.Service.VoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.czc.Constant.NSFW.NSFW_BAN;

@Service
public class RecycleServiceImpl implements RecycleService {

    @Autowired
    private RecycleMapper recycleMapper;
    @Autowired
    private VoService voService;

    @Override
    // 根据文件id恢复文件
    public boolean recycleFile(String fileId) {
        // 通过文件id查询用户文件关系
        User2FileDTO u2f = recycleMapper.selectU2F(fileId);
        // 通过文件id查询文件
        FileEntity file = recycleMapper.selectFileByU2FId(fileId);
        // 如果文件被禁用，则不允许恢复
        if (file.getIsBan() == NSFW_BAN) {
            return false;
        }
        // 设置文件状态为未删除
        u2f.setIsDelete(0);
        // 恢复文件
        return recycleMapper.recycleFile(fileId) > 0 ? true : false;
    }


    @Override
    public boolean removeFile(String fileId) {
        return recycleMapper.deleteFile(fileId) > 0 ? true : false;
    }

}
