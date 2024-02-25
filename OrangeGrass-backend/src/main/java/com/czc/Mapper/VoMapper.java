package com.czc.Mapper;

import com.czc.Entity.DTO.EntityListDTO;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.DTO.User2FolderDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VoMapper {

    public User2FileDTO selectUser2FileDeleted(String u2fId);

    public List<User2FileDTO> selectDeletedFiles(String userId);

    public List<EntityListDTO> selectEntityByFolder(String folderId);

    public int insertU2F(User2FileDTO u2f);

    public int deleteFolder2X(String id);

    public int deleteUser2File(String id);

    public List<Folder2XDTO> selectFolder2XVOByFolderId(String folderId);

    public int updateU2FVO(User2FileDTO vo);

    public User2FileDTO getUser2FileVOById(String id);

    public List<User2FileDTO> getUser2FileVOByUserId(String userId);

    public int updateU2FVOByFileId(User2FileDTO vo);

    public List<Folder2XDTO> getFolder2XVObyUserId(String userId);

    public int insertUserFolder(User2FolderDTO vo);
}
