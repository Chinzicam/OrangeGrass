package com.czc.Service;

import com.czc.Entity.UserStorage;

public interface StorageService {

    public Long getStorage(String userId);

    public UserStorage getUserStorage(String userId);
}
