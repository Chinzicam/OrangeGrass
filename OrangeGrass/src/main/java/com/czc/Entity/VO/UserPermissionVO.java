package com.czc.Entity.VO;

import lombok.Data;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;

@Data
public class UserPermissionVO {
    private String userId;
    private List<PermissionTreeVO> permissions;
}
