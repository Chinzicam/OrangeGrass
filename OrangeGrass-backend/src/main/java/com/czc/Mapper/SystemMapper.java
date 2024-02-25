package com.czc.Mapper;

import com.czc.Entity.AccessRecord;
import com.czc.Entity.ConfigEntity;
import com.czc.Entity.ConfigItemEntity;
import com.czc.Entity.DTO.ConfigItemDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface SystemMapper {

    public int updateAdvertisementLength(int length);

    public List<AccessRecord> selectInterfaceAccessRecord();

    public int insertInterfaceAccessRecord(String uri,
                                           String className,
                                           String methodName,
                                           Long costTime,
                                           Date accessTime,
                                           String fromIp);

    public int selectAdvertisementLength();

    public double selectNSFWScore();

    public int updateNSFWScore(double score);

    public Long selectSpeedLimitRate();

    public int updateSpeedLimit(Long rate);

    public int deleteById(String id);

    public ConfigItemEntity selectConfigItemByName(String name);

    public int insertConfigItem(String id, String name);

    public int updateConfigItem(ConfigItemEntity config);

    public List<ConfigItemEntity> selectConfigItems();

    public int insertIntoSystemConfig(String configId,String itemId,String itemValue);

    public int insertIntoConfigItem(String id,String name,String value);

    public List<ConfigItemDTO> selectConfigItemById(String id);

    public int insertConfig(ConfigEntity config);

    public List<ConfigEntity> selectConfigs();
}
