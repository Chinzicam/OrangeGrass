package com.czc.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czc.Entity.PermissionEntity;
import com.czc.Entity.VO.PermissionTreeVO;

import java.util.List;

public interface PermissionService extends IService<PermissionEntity> {

    public boolean saveUserPermission(String userId, List<PermissionTreeVO> permissions);

    public List<PermissionTreeVO> getUserPermission(String userId);

    public boolean savePermissions (List<PermissionTreeVO> permissions);

    public List<PermissionTreeVO> getPerMissionTreeVO();
}
