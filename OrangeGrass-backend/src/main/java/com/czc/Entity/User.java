package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
@ApiModel("用户实体")
public class User {

    @TableId("user_id")
    @ApiModelProperty("用户ID")
    private String userId;

    @TableField("username")
    @ApiModelProperty("用户名")
    private String username;

    @TableField("password")
    @ApiModelProperty("用户密码")
    private String password;

    @TableField("create_time")
    @ApiModelProperty("用户账号创建时间")
    private Date createTime;

    @TableField("update_time")
    @ApiModelProperty("用户账号修改时间")
    private Date updateTime;

    @TableField(value = "is_delete")
    @TableLogic(value = "0" , delval = "1")
    @ApiModelProperty("用户账号是否被删除")
    private int isDelete;

    @TableField(value = "is_ban")
    @ApiModelProperty("用户账号是否被禁用")
    private int isBan;

    @TableField(value = "storage")
    @ApiModelProperty("用户当前云盘容量")
    private Long storage;

    @TableField(value = "user_type")
    @ApiModelProperty("用户类型")
    private int userType;

    @ApiModelProperty("用户上次续费时间")
    @TableField(value = "user_membership_last_renewal")
    private Date userMembershipLastRenewal;

    @ApiModelProperty("用户会员过期时间")
    @TableField(value = "user_membership_expire")
    private Date userMembershipExpire;
}
