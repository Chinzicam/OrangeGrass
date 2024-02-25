package com.czc.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czc.Entity.DTO.UserPermissionDTO;
import com.czc.Entity.PermissionEntity;
import com.czc.Entity.VO.PermissionTreeVO;
import com.czc.Mapper.PermissionMapper;
import com.czc.Service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionEntity> implements PermissionService {


    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public boolean saveUserPermission(String userId,
                                      List<PermissionTreeVO> permissions) {
        Map<String,String> res = new HashMap<>();
//        for (PermissionTreeVO x : permissions) {
//            createUserPermissionList(userId,x,res);
//        }
        for (PermissionTreeVO vo : permissions) {
            res.put(vo.getId(),userId);
        }
        permissionMapper.deleteUserPermission(userId);
        if (!permissionMapper.saveUserPermission(res)) {
            return false;
        }
        return true;
    }

    @Override
    public List<PermissionTreeVO> getUserPermission(String userId) {
        List<UserPermissionDTO> userPermission = permissionMapper.selectUserPermission(userId);
        List<PermissionEntity> permissions = list();
        List<PermissionEntity> newList = new ArrayList<>();
        for (PermissionEntity permission : permissions) {
            for (UserPermissionDTO tmp : userPermission) {
                if (tmp.getPermissionId().equals(permission.getPermissionId())) {
                    newList.add(permission);
                    continue;
                }
            }
        }
        List<PermissionTreeVO> res = getPermissionTreeRoot(newList);
        for (PermissionTreeVO tmp : res) {
            createPermissionTreeVo(tmp,newList);
        }
        return res;
    }


    @Override
    public boolean savePermissions (List<PermissionTreeVO> permissions) {
        for (PermissionTreeVO permission : permissions) {
            try {
                walkPermissionVOForSave(permission);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public List<PermissionTreeVO> getPerMissionTreeVO() {
        List<PermissionEntity> permissions = list();
        List<PermissionTreeVO> roots = getPermissionTreeRoot(permissions);
        for (PermissionTreeVO root : roots) {
            createPermissionTreeVo(root,permissions);
        }
        return roots;
    }

    private void createUserPermissionList(String userId,
                                          PermissionTreeVO root,
                                          Map<String,String> res) {
        if (root == null){
            return;
        }
        res.put(userId,root.getId());
        for (PermissionTreeVO x : root.getChildren()) {
            createUserPermissionList(userId,x,res);
        }
    }

    private void walkPermissionVOForSave(PermissionTreeVO root) throws Exception {
        if (root == null){
            return;
        }
        if (root.getChildren() != null && root.getChildren().size() > 0) {
            for (PermissionTreeVO vo : root.getChildren()) {
                if (vo != null && vo.getId() == null) {
                    PermissionEntity permission = new PermissionEntity();
                    permission.setPermissionName(vo.getLabel());
                    permission.setParentId(root.getId());
                    String id = UUID.randomUUID().toString();
                    permission.setPermissionId(id);
                    vo.setId(id);
                    if (!saveOrUpdate(permission)) {
                        throw new Exception("插入权限时发生错误");
                    }
                }
            }
        }
        for (PermissionTreeVO subs : root.getChildren()) {
            walkPermissionVOForSave(subs);
        }
    }

    private void createPermissionTreeVo(PermissionTreeVO root,
                                                          List<PermissionEntity> permissions) {
        if (root == null) {
            return;
        }
        for (PermissionEntity permission : permissions) {
            if (root.getId().equals(permission.getParentId())) {
                List<PermissionTreeVO> tmpSubs = root.getChildren() == null ?
                                                new ArrayList<>() : root.getChildren();
                PermissionTreeVO tmpSub = new PermissionTreeVO();
                tmpSub.setId(permission.getPermissionId());
                tmpSub.setLabel(permission.getPermissionName());
                tmpSub.setChildren(new ArrayList<>());
                tmpSubs.add(tmpSub);
            }
        }
        for (PermissionTreeVO next : root.getChildren()) {
            createPermissionTreeVo(next,permissions);
        }
    }

    private List<PermissionTreeVO> getPermissionTreeRoot(List<PermissionEntity> permissions){
        List<PermissionTreeVO> tmp = new ArrayList<>();
        for (PermissionEntity permission : permissions){
            if (permission.getParentId() == null){
                PermissionTreeVO vo = new PermissionTreeVO();
                vo.setId(permission.getPermissionId());
                vo.setLabel(permission.getPermissionName());
                vo.setChildren(new ArrayList<>());
                tmp.add(vo);
            }
        }
        return tmp;
    }
}
