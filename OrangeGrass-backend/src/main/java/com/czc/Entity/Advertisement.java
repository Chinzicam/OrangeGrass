package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("advertisement")
public class Advertisement {
    @TableId("advertisement_id")
    private String advertisementId;
    private String advertisementLocation;
    private Long advertisementSize;
    private String advertisementDescription;
    private String advertisementName;
    private String advertisementThumbnail;
}
