package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("file")
public class FileEntity {

    @TableId(value = "file_id")
    private String id;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "md5")
    private String md5;

    @TableField(value = "file_location")
    private String fileLocation;

    @TableField(value = "file_size")
    private Long fileSize;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField(value = "minetype")
    private String minetype;

    @TableField(value = "is_delete")
    @TableLogic(value = "0" , delval = "1")
    private int isDelete;
    @TableField(value = "nsfw_score")
    private double nsfwScore;
    @TableField(value = "is_ban")
    private int isBan;
}
