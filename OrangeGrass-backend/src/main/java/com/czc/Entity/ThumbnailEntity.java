package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("thumbnail")
public class ThumbnailEntity {

    @TableId("id")
    private String id;

    @TableField("name")
    private String name;

    @TableField("file_id")
    private String fileId;
}
