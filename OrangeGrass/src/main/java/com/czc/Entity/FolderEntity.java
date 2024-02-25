package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Hanaue
 */
@Data
@TableName("folder")
public class FolderEntity {

    @TableId("folder_id")
    private String FolderId;

    @JsonProperty("FolderName")
    @TableField("folder_name")
    private String FolderName;

    @JsonProperty("FolderSize")
    @TableField("folder_size")
    private Long FolderSize;

    @TableField("update_time")
    private Date updateTime;

    @TableField("create_time")
    private Date createTime;

    @TableField("is_delete")
    @TableLogic(value = "0" , delval = "1")
    private int isDelete;

    @TableField("is_main")
    private int isMain;
}
