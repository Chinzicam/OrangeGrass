package com.czc.Controller;


import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Service.UserFriendService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    private UserFriendService userFriendService;


    @CostTime
    @GetMapping("/search")
    public HttpResonse searchUser(@Param("username") String username){
        return HttpResonse.success().setMsg("查询用户成功")
                .setData(userFriendService.getUserByUsername(username));
    }

    @CostTime
    @GetMapping("")
    public HttpResonse getFriendList(@Param("userId") String userId) {
        return HttpResonse.success().setMsg("查询好友列表成功")
                .setData(userFriendService.getFriendVOList(userId));
    }
}
