package com.czc.Service.Impl;

import com.czc.Entity.AccessRecord;
import com.czc.Entity.ConfigEntity;
import com.czc.Entity.ConfigItemEntity;
import com.czc.Entity.DTO.ConfigItemDTO;
import com.czc.Mapper.SystemMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.czc.Service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.czc.Constant.NSFW.NSFW_IMG_CHECK_ENABLE;
import static com.czc.Constant.SystemConstant.*;

@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private SystemMapper systemMapper;

    @Override
    public boolean setAdvertisementLength(int length){
        return systemMapper.updateAdvertisementLength(length) == 1 ? true : false;
    }

    @Override
    public PageInfo<AccessRecord> getInterfaceAccessRecord(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<AccessRecord> res = systemMapper.selectInterfaceAccessRecord();
        return new PageInfo<AccessRecord>(res);
    }

    @Override
    public boolean addInterfaceAccessRecord(String uri,
                                            String className,
                                            String methodName,
                                            Long costTime,
                                            Date accessTime,
                                            String fromIp) {
        return systemMapper.insertInterfaceAccessRecord(uri,className,methodName,costTime,accessTime,fromIp) > 0 ? true : false;
    }

    @Override
    public double getNSFWScore(){
        return systemMapper.selectNSFWScore();
    }

    @Override
    public int getAdvertisementLength() {
        return systemMapper.selectAdvertisementLength();
    }

    @Override
    public boolean setNSFWScore(double score){
        return systemMapper.updateNSFWScore(score) > 0 ? true : false;
    }

    @Override
    public Long getSpeedLimitRate() {
        return systemMapper.selectSpeedLimitRate();
    }

    @Override
    public boolean setSpeedLimit(Long rate){
        return systemMapper.updateSpeedLimit(rate) > 0 ? true : false;
    }

    @Override
    public boolean removeById(String id) {
        return systemMapper.deleteById(id) > 0 ? true : false;
    }

    @Override
    public boolean isImgCheckEnabled() {
        return systemMapper.selectConfigItemByName(NSFW_IMG_CHECK_ENABLE).getValue() ==
                SYSTEM_ENABLED ? true : false;
    }

    @Override
    public boolean isFileUploadEnabled() {
        return systemMapper.selectConfigItemByName(FILE_UPLOAD).getValue() == SYSTEM_ENABLED;
    }

    @Override
    public boolean isSpeedLimitEnabled() {
        return systemMapper.selectConfigItemByName(SPEED_LIMIT_NAME).getValue() ==
                SYSTEM_ENABLED ? true : false;
    }

    @Override
    public boolean isAdvertisementEnable() {
        return systemMapper.selectConfigItemByName(PLAY_ADVERTISEMENT_BEFORE_VIDEO).getValue() ==
                SYSTEM_ENABLED ? true : false;
    }

    @Override
    public boolean addConfigItem(String name) {
        return systemMapper.insertConfigItem(UUID.randomUUID().toString().replaceAll("-",""),name) > 0 ?
                true : false;
    }

    @Override
    public boolean modifyConfigItem(ConfigItemEntity config) {
        return systemMapper.updateConfigItem(config) > 0 ? true : false;
    }

    @Override
    public List<ConfigItemEntity> getConfigItems() {
        return systemMapper.selectConfigItems();
    }

    @Override
    public boolean addItemForConfig(String configId, String itemId, String itemValue) {
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        return systemMapper.insertIntoSystemConfig(configId,itemId,itemValue) > 0 ? true : false;
    }

    @Override
    public List<ConfigItemDTO> getDetail(String id) {
        return systemMapper.selectConfigItemById(id);
    }

    @Override
    public boolean addConfig(String name, String userId) {
        ConfigEntity config = new ConfigEntity();
        Date currentTime = new Date();
        config.setCreateTime(currentTime);
        config.setUpdateTime(currentTime);
        config.setUpdateUserId(userId);
        config.setCreateUserId(userId);
        config.setName(name);
        System.out.println(config);
        return systemMapper.insertConfig(config) > 0 ? true : false;
    }

    @Override
    public List<ConfigEntity> getConfigs() {
        return systemMapper.selectConfigs();
    }
}
