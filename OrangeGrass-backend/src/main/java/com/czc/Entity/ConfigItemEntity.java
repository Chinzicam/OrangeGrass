package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("config_item")
public class ConfigItemEntity {

    @TableId("config_item_id")
    private String id;
    @TableField("config_item_name")
    private String name;
    @TableField("config_item_value")
    private int value;
    @TableField("config_item_extra")
    private String extra;

}
