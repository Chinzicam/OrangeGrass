package com.czc.Service;

import com.czc.Entity.AccessRecord;
import com.czc.Entity.ConfigEntity;
import com.czc.Entity.ConfigItemEntity;
import com.czc.Entity.DTO.ConfigItemDTO;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

public interface SystemService {

    public boolean setAdvertisementLength(int length);

    public PageInfo<AccessRecord> getInterfaceAccessRecord(Integer pageNum, Integer pageSize);

    public boolean addInterfaceAccessRecord(String uri,
                                            String className,
                                            String methodName,
                                            Long costTime,
                                            Date accessTime,
                                            String fromIp);
    public double getNSFWScore();
    public int getAdvertisementLength();
    public boolean isAdvertisementEnable();
    public boolean setNSFWScore(double score);
    public Long getSpeedLimitRate();
    public boolean setSpeedLimit(Long rate);
    public boolean removeById(String id);
    public boolean isFileUploadEnabled();
    public boolean isImgCheckEnabled();
    public boolean isSpeedLimitEnabled();
    public boolean addConfigItem(String name);
    public boolean modifyConfigItem(ConfigItemEntity config);
    public List<ConfigItemEntity> getConfigItems();
    public boolean addItemForConfig(String configId,String itemId,String itemValue);
    public List<ConfigItemDTO> getDetail(String id);
    public boolean addConfig(String name,String userId);
    public List<ConfigEntity> getConfigs();
}
