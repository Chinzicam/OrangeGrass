package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.User;

public interface UserService extends IService<User> {

    public boolean isMembership(String userId);

    public boolean isAdmin(String userId);

    public boolean createMainFolder(User user);

    public boolean addFile(User2FileDTO vo);

}
