package com.czc.Service.Impl;

import com.czc.Entity.DTO.EntityListDTO;
import com.czc.Entity.DTO.Folder2XDTO;
import com.czc.Entity.DTO.User2FileDTO;
import com.czc.Entity.FileEntity;
import com.czc.Mapper.VoMapper;
import com.czc.Service.FileService;
import com.czc.Service.VoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoServiceImpl implements VoService {

    @Autowired
    private VoMapper voMapper;
    @Autowired
    private FileService fileService;

    @Override
    public User2FileDTO getDeletedU2F(String u2fId){
        return voMapper.selectUser2FileDeleted(u2fId);
    }

    @Override
    public List<User2FileDTO> getDeletedFiles(String userId) {
        return voMapper.selectDeletedFiles(userId);
    }

    @Override
    public List<EntityListDTO> getEntityList(String folderId) {
        return voMapper.selectEntityByFolder(folderId);
    }

    @Override
    public boolean addU2F(User2FileDTO u2f) {
        if (voMapper.insertU2F(u2f) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteUser2File(String id){
//        User2FileDTO u2f = voMapper.getUser2FileVOById(id);
//        List<Folder2XDTO> f2xs = voMapper.getFolder2XVObyUserId(u2f.getUserId());
//        for (Folder2XDTO f2x : f2xs){
//            if (f2x.getXId().equals(u2f.getId())){
//                voMapper.deleteFolder2X(f2x.getId());
//            }
//        }
        if (voMapper.deleteUser2File(id) > 0){
            return true;
        }
        return false;
    }


    @Override
    public List<Folder2XDTO> getFolder2XDTObyFolderId(String folderId){
        return voMapper.selectFolder2XVOByFolderId(folderId);
    }

    @Override
    public boolean updateU2FDTO(User2FileDTO vo){
        if (voMapper.updateU2FVO(vo) > 0){
            return true;
        }
        return false;
    }

    @Override
    public User2FileDTO selectUser2FileDTOById(String id){
        return voMapper.getUser2FileVOById(id);
    }

    @Override
    public List<User2FileDTO> selectUser2FileDTOByUserId(String userId){
        return voMapper.getUser2FileVOByUserId(userId);
    }

    @Override
    public boolean updateU2FDTOFileName(User2FileDTO vo){
        if (voMapper.updateU2FVOByFileId(vo) > 1){
            return true;
        }
        return false;
    }

    @Override
    public List<Folder2XDTO> selectFolder2XDTObyUserId(String userId){
        return voMapper.getFolder2XVObyUserId(userId);
    }

}
