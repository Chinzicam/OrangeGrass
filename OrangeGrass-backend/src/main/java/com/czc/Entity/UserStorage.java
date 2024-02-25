package com.czc.Entity;

import lombok.Data;

@Data
public class UserStorage {

    private String userId;
    private Long storage;
    private Long usedStorage;
}
