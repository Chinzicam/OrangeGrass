package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @author czc
 */
@Data
@TableName("share_record")
public class ShareRecord {

    @TableId("share_id")
    private String id;

    @TableField("x_id")
    private String xId;

    @TableField("x_name")
    private String xName;

    @TableField("x_type")
    private String xType;

    @TableField("from_user_id")
    private String fromUserId;

    /**
     * 需要提取码
     * 或者无需提取码
     */
    @TableField("share_type")
    private int ShareType;

    @TableField("extract_code")
    private String extractCode;

    @TableField("create_time")
    private Date createTime;

    @TableField("expire_time")
    private Date expireTime;

    @TableField("status")
    private int status;


}
