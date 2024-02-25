package com.czc.Entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
public class AccessRecord {
    private String className;
    private String methodName;
    private Long costTime;
    private Date accessTime;
    private String fromIp;
    private String location;
    private String uri;
}
