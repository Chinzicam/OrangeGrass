package com.czc.Service.Impl;

import com.czc.Entity.UserStorage;
import com.czc.Mapper.StorageMapper;
import com.czc.Service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Override
    public Long getStorage(String userId) {
        return storageMapper.selectUsedStorageByUserId(userId);
    }

    @Override
    public UserStorage getUserStorage(String userId) {
        return storageMapper.selectUserStorage(userId);
    }
}
