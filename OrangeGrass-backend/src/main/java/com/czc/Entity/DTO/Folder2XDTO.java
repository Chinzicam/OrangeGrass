package com.czc.Entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("folder_x")
public class Folder2XDTO {

    @TableId("id")
    private String id;

    @TableField("folder_id")
    private String folderId;

    @TableField("x_id")
    private String xId;

    @TableField("x_type")
    private String xType;

    @TableField("is_delete")
    @TableLogic(value = "0" , delval = "1")
    private int isDelete;


}
