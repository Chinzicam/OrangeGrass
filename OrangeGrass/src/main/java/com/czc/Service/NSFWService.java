package com.czc.Service;

import com.czc.Entity.FileEntity;
import com.czc.Entity.VO.NSFWVO;

import java.util.List;

public interface NSFWService {
    public Object checkImages(List<FileEntity> files) throws Exception;
    public Object checkImage(String id) throws Exception;
    public NSFWVO checkVideo(String id) throws Exception;
}
