package com.czc.Service.Impl;

import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.FileEntity;
import com.czc.Mapper.RecycleMapper;
import com.czc.Service.FileService;
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
    public boolean recycleFile(String fileId) {
        User2FileDTO u2f = recycleMapper.selectU2F(fileId);
        FileEntity file = recycleMapper.selectFileByU2FId(fileId);
        if (file.getIsBan() == NSFW_BAN) {
            return false;
        }
        u2f.setIsDelete(0);
        return recycleMapper.recycleFile(fileId) > 0 ? true : false;
    }


    @Override
    public boolean removeFile(String fileId) {
        return recycleMapper.deleteFile(fileId) > 0 ? true : false;
    }

}
