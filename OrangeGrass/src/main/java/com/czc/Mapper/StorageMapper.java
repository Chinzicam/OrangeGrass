package com.czc.Mapper;

import com.czc.Entity.UserStorage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StorageMapper {

    public Long selectUsedStorageByUserId(String userId);

    public UserStorage selectUserStorage(String userId);

}
