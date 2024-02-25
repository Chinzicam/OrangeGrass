package com.czc.Entity.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class ShareRecordVO {

    private String id;

    private String xId;

    private String xName;

    private String xType;

    private String fromUserId;

    /**
     * 需要提取码
     * 或者无需提取码
     */
    private int shareType;

    private String extractCode;

    private Date createTime;

    private Date expireTime;

    private int status;

    private int extractCount;

}
