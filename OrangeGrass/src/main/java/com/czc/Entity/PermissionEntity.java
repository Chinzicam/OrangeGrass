package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("permission")
public class PermissionEntity {

    @TableId("permission_id")
    private String permissionId;

    @TableField("permission_name")
    private String permissionName;

    @TableField("parent_id")
    private String parentId;

}
