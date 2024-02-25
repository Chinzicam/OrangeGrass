package com.czc.Entity.DTO;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_folder")
public class User2FolderDTO {

    @TableId("id")
    private String id;

    @TableField("user_id")
    private String userId;

    @TableField("folder_id")
    private String folderId;

    @TableField("is_delete")
    @TableLogic(value = "0" , delval = "1")
    private int isDelete;
}
