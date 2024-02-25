package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Entity.PermissionEntity;
import com.czc.Entity.VO.PermissionTreeVO;
import com.czc.Entity.VO.UserPermissionVO;
import com.czc.Service.PermissionService;
import com.czc.Constant.HttpResonse;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("")
    public HttpResonse getAll() {
        return HttpResonse.success().setData(permissionService.getPerMissionTreeVO());
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("/user")
    public HttpResonse updatePermission(@RequestBody UserPermissionVO allocatePermission){
        permissionService.saveUserPermission(allocatePermission.getUserId(),
                allocatePermission.getPermissions());
        return HttpResonse.success();
    }

    @RequiresRoles("admin")
    @CostTime
    @PostMapping("/user")
    public HttpResonse saveUserPermission(@Param("userId") String userId,
                                          @RequestBody List<PermissionTreeVO> permissionTree){
//        savePermission(permissionTree);
        return HttpResonse.success();
    }

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/user")
    public HttpResonse getUserPermission(@Param("userId") String userId) {
//        System.out.println(userId);
        return HttpResonse.success().setData(permissionService.getUserPermission(userId));
    }

    @RequiresRoles("admin")
    @CostTime
    @DeleteMapping("")
    public HttpResonse deleteOne(@Param("permId") String permId) {
        if (permissionService.removeById(permId)) {
            return HttpResonse.success().setMsg("删除权限成功");
        }
        return HttpResonse.fail().setMsg("删除权限失败");
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("")
    public HttpResonse updateOne(@Param("permId") String permId,
                                 @Param("newName") String newName) {
        PermissionEntity permission = permissionService.getById(permId);
        permission.setPermissionName(newName);
        if (permissionService.updateById(permission)) {
            return HttpResonse.success().setMsg("修改权限名成功");
        }
        return HttpResonse.fail().setMsg("修改权限名失败");
    }

    @RequiresRoles("admin")
    @CostTime
    @PostMapping("")
    public HttpResonse savePermission(@RequestBody List<PermissionTreeVO> permissions) {
        if (permissionService.savePermissions(permissions)) {
            return HttpResonse.success().setMsg("保存权限成功");
        }
        return HttpResonse.fail("保存权限失败");
    }

    @RequiresRoles("admin")
    @CostTime
    @PostMapping("/")
    public HttpResonse savePrimaryPermission(@Param("name") String name) {
        PermissionEntity permission = new PermissionEntity();
        permission.setPermissionName(name);
        permission.setPermissionId(UUID.randomUUID().toString());
        if (permissionService.save(permission)) {
            return HttpResonse.success().setMsg("新增成功");
        }
        return HttpResonse.fail().setMsg("新增失败");
    }

}
