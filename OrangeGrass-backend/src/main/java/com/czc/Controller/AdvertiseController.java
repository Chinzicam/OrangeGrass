package com.czc.Controller;

import com.czc.Config.annotation.CostTime;
import com.czc.Constant.FileConstant;
import com.czc.Entity.Advertisement;
import com.czc.Service.AdvertiseService;
import com.czc.Service.SystemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.czc.Constant.HttpResonse;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RequestMapping("/api/advertise")
@RestController
public class AdvertiseController {

    @Autowired
    private SystemService systemService;
    @Autowired
    private AdvertiseService advertiseService;

    @RequiresRoles("admin")
    @CostTime
    @GetMapping("")
    public HttpResonse getAdvertisements(@Param("pageSize") int pageSize,
                                         @Param("pageNum") int pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        List<Advertisement> advertisements = advertiseService.list();
        return HttpResonse.success().setMsg("查询广告成功").setData(new PageInfo<Advertisement>(advertisements));
    }

    @GetMapping("/thumbnail/{id}")
    public void getThumbnail(@PathVariable("id") String id,
                             HttpServletResponse response) {
        advertiseService.downloadThumbnail(id,response);
    }

    @GetMapping("/video/{id}")
    public void getVideo(@PathVariable("id") String id,
                         HttpServletResponse response,
                         HttpServletRequest request) {
        advertiseService.chunkDownload(id,response,request);
    }

    @RequiresRoles("admin")
    @CostTime
    @PostMapping("")
    public HttpResonse createAdvertisement(@Param("name") String name,
                                           @Param("description") String description,
                                           @Param("file")MultipartFile file) {
        if (advertiseService.addAdvertisement(name,description,file)) {
            return HttpResonse.success("添加广告成功");
        }
        return HttpResonse.fail("添加广告失败");
    }

    @CostTime
    @GetMapping("/random")
    public HttpResonse getRandomAdvertisement() {
        List<Advertisement> advertisements = advertiseService.list();
        int index = new Random().nextInt(advertisements.size());
        Advertisement advertisement = advertisements.get(index);
        Map<String,Object> result = new HashMap<>();
        result.put("playAdvertisement",systemService.isAdvertisementEnable());
        result.put("advertisementUrl","http://" + FileConstant.SERVER_IP + "/api/advertise/video/"+advertisement.getAdvertisementId());
        result.put("advertisementLength",systemService.getAdvertisementLength());
        return HttpResonse.success().setMsg("查询视频广告链接成功").setData(result);
    }

}
