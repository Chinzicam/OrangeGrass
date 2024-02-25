package com.czc.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czc.Entity.DTO.UserPermissionDTO;
import com.czc.Entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.bytedeco.opencv.presets.opencv_core;

import java.util.List;
import java.util.Map;

@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {

    public int deleteUserPermission(String userId);

    public boolean saveUserPermission(@Param("map") Map<String, String> map);

    public List<UserPermissionDTO> selectUserPermission(String userId);

}
