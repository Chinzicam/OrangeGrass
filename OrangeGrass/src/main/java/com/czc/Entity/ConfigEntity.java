package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("config")
public class ConfigEntity {

    @TableId("config_id")
    private String id;
    @TableField("config_name")
    private String name;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
    @TableField("create_user_id")
    private String createUserId;
    @TableField("update_user_id")
    private String updateUserId;
}
