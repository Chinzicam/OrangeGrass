package com.czc.Service;

import com.czc.Entity.DTO.EntityListDTO;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.DTO.User2FileDTO;

import java.util.List;

public interface VoService {

    public User2FileDTO getDeletedU2F(String u2fId);

    public List<User2FileDTO> getDeletedFiles(String userId);

    public List<EntityListDTO> getEntityList(String folderId);

    public boolean addU2F(User2FileDTO u2f);

    public boolean deleteUser2File(String id);

    public List<Folder2XDTO> getFolder2XDTObyFolderId(String folderId);

    public boolean updateU2FDTO(User2FileDTO vo);

    public User2FileDTO selectUser2FileDTOById(String id);

    public List<User2FileDTO> selectUser2FileDTOByUserId(String userId);

    public boolean updateU2FDTOFileName(User2FileDTO vo);

    public List<Folder2XDTO> selectFolder2XDTObyUserId(String userId);
}
