package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.AccessRecord;
import com.czc.Entity.ConfigItemEntity;
import com.czc.Service.SystemService;
import com.github.pagehelper.PageInfo;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private SystemService systemService;
    @Autowired
    private Ip2regionSearcher ip2regionSearcher;

    @RequiresRoles("admin")
    @GetMapping("/accesslog")
    public HttpResonse getAccessRecord(@Param("pageNum") Integer pageNum,
                                       @Param("pageSize") Integer pageSize){
        PageInfo pageInfo = systemService
                .getInterfaceAccessRecord(pageNum,pageSize);
        List<AccessRecord> result = pageInfo.getList();
        for (AccessRecord tmp : result) {
            tmp.setLocation(ip2regionSearcher.getAddress(tmp.getFromIp()));
        }
        return HttpResonse.success().setMsg("查询接口日志成功")
                .setData(pageInfo);
    }

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/nsfw")
    public HttpResonse getNSFWScore() {
        return HttpResonse.success().setMsg("查询nsfw值成功").setData(systemService.getNSFWScore());
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("/nsfw")
    public HttpResonse setNSFWScore(@Param("score") double score){
        return systemService.setNSFWScore(score) ?
                HttpResonse.success().setMsg("修改nsfw值成功") :
                HttpResonse.fail().setMsg("修改nsfw值失败");
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("/adlength")
    public HttpResonse setAdvertisementLength(@Param("advertisementLength") int advertisementLength){
        return systemService.setAdvertisementLength(advertisementLength) ?
                HttpResonse.success().setMsg("修改广告投放时长成功") :
                HttpResonse.fail().setMsg("修改广告投放时长失败");
    }

    @CostTime
    @GetMapping("/speed")
    public HttpResonse getSpeedLimitRate() {
        return HttpResonse.success().setMsg("查询限速成功").setData(systemService.getSpeedLimitRate());
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("/speed")
    public HttpResonse setSpeedLimitRate(@Param("rate") Long rate){
        return systemService.setSpeedLimit(rate) ?
                HttpResonse.success().setMsg("修改限速值成功") :
                HttpResonse.fail().setMsg("修改限速值失败");
    }


    @RequiresRoles("admin")
    @CostTime
    @DeleteMapping("/item")
    public HttpResonse deleteById(@Param("id") String id) {
        if (systemService.removeById(id)) {
            return HttpResonse.success().setMsg("删除成功");
        }
        return HttpResonse.fail().setMsg("删除失败");
    }


    @RequiresRoles("admin")
    @CostTime
    @PutMapping("/item")
    public HttpResonse modifyItem(@RequestBody ConfigItemEntity config) {
        if (!systemService.modifyConfigItem(config)) {
            return HttpResonse.fail().setMsg("修改失败");
        }
        return HttpResonse.success().setMsg("修改成功");
    }


    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/items")
    public HttpResonse getItems() {
        return HttpResonse.success().setData(systemService.getConfigItems());
    }


    @RequiresRoles("admin")
    @CostTime
    @PostMapping("/item")
    public HttpResonse addItemForConfig(@Param("name") String name) {
        if (!systemService.addConfigItem(name)) {
            return HttpResonse.fail().setMsg("新增设置项出错");
        }
        return HttpResonse.success().setMsg("新增设置项成功");
    }


    @RequiresRoles("admin")
    @CostTime
    @GetMapping("/detail")
    public HttpResonse getSystemConfigDetail(@Param("id") String id) {
        return HttpResonse.success().setData(systemService.getDetail(id));
    }


    @RequiresRoles("admin")
    @CostTime
    @GetMapping("")
    public HttpResonse getSystemConfigs(){
        return HttpResonse.success().setData(systemService.getConfigs()).setMsg("获取系统设置成功");
    }


    @RequiresRoles("admin")
    @CostTime
    @PostMapping("")
    public HttpResonse addConfig(@Param("name") String name,
                                 @Param("userId") String userId) {
        if (!systemService.addConfig(name,userId)){
            return HttpResonse.success().setMsg("添加系统设置发生错误");
        }
        return HttpResonse.success().setMsg("添加系统设置成功");
    }
}
