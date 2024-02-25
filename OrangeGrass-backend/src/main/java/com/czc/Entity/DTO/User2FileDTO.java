package com.czc.Entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_file")
public class User2FileDTO {

    @TableId("id")
    private String id;

    @TableField("user_id")
    private String UserId;

    @TableField("file_id")
    private String FileId;

    @TableField("file_name")
    private String FileName;

    @TableField("is_delete")
    @TableLogic(value = "0" , delval = "1")
    private int isDelete;
}
