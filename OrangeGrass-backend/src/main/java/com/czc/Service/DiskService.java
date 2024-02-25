package com.czc.Service;

import com.czc.Entity.VO.EntityTreeVO;

import java.util.List;

public interface DiskService {

    public List<EntityTreeVO> getFolderAndFileByFolderId(String folderId);

    public EntityTreeVO createFolderAndFileTreeByType (String userId,String folderId,List<String> typeList);

    public EntityTreeVO createFolderTree (String userId);

    public EntityTreeVO createFolderAndFileTree (String userId , String folderId);
}
