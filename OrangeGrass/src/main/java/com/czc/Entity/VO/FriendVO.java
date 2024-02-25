package com.czc.Entity.VO;

import lombok.Data;

import java.util.Date;

@Data
public class FriendVO {
    private int id;
    private String userId;
    private String username;
    private String friendId;
    private String friendUsername;
    private Date createTime;
    private int unreadMessageNumber;
}
