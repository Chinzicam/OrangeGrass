package com.czc.Controller;

import com.czc.Service.ThumbnailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/**
 * @author czc
 */
@RestController
@RequestMapping("/api/thumbnail")
public class ThumbnailController {

    @Autowired
    private ThumbnailService thumbnailService;

    @GetMapping("/{id}")
    public void downloadThumbnail(@PathVariable("id") String id,
                                  HttpServletResponse response){
        thumbnailService.downloadThumbnail(id,response);
    }

    @GetMapping("/admin/{id}")
    public void downloadThunbnailForAdmin(@PathVariable("id") String id,
                                          HttpServletResponse response){
        thumbnailService.downloadThumbnailForAdmin(id,response);
    }

}
