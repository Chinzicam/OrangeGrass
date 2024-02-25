package com.czc.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.czc.Config.annotation.CostTime;
import com.czc.Constant.HttpResonse;
import com.czc.Entity.User;
import com.czc.Service.UserService;
import com.czc.Util.JWT.JwtToken;
import com.czc.Util.JWT.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Hanaue
 */
@RestController
@RequestMapping("/api/user")
@Api(value = "用户接口", tags = {"用户接口"})
public class UserController {

    @Autowired
    private UserService userService;

    @CostTime
    @GetMapping("/membership")
    public HttpResonse getUserMembership(@Param("userId") String userId) {
        User user = userService.getById(userId);
        if (user.getUserMembershipExpire() != null && user.getUserMembershipExpire().after(new Date())) {
            return HttpResonse.success().setMsg("该用户是会员").setData(true);
        }
        return HttpResonse.success().setMsg("该用户不是会员").setData(false);
    }

    @CostTime
    @PutMapping("/membership")
    public HttpResonse setUserMembership(@Param("userId") String userId) {
        Date now = new Date();
        Date expireTime = new Date();
        User user = userService.getById(userId);
        user.setUserMembershipLastRenewal(now);
        expireTime.setTime(now.getTime() + 1000*30);
        user.setUserMembershipExpire(expireTime);
        if (userService.updateById(user)) {
            return HttpResonse.success().setMsg("已为该用户添加30秒会员");
        }
        return HttpResonse.fail().setMsg("充值失败");
    }

    @RequiresRoles("admin")
    @CostTime
    @PutMapping("")
    public HttpResonse modifyUser(@RequestBody User user) {
        if (userService.updateById(user)) {
            return HttpResonse.success().setMsg("修改用户信息成功");
        }
        return HttpResonse.fail().setMsg("修改用户信息失败");
    }

    @ApiOperation("判断用户是不是管理员")
    @CostTime
    @GetMapping("/isadmin")
    public HttpResonse isAdmin(){
        Subject subject = SecurityUtils.getSubject();
        User user = (User)subject.getPrincipal();
        System.out.println(user.getUserId());
        return HttpResonse.success().setMsg("查询身份成功").setData(userService.isAdmin(user.getUserId()));
    }

    @RequiresRoles("admin")
    @ApiOperation("获取所有用户列表（管理员）")
    @CostTime
    @GetMapping("/admin")
    public HttpResonse getAll(){
        return HttpResonse.success().setData(userService.list());
    }

    @ApiOperation("根据用户ID获取用户名")
    @CostTime
    @GetMapping("/name")
    public HttpResonse getName(@Param("id") String id) {
        User user = userService.getById(id);
        return HttpResonse.success().setData(user.getUsername());
    }

    @ApiOperation("新增用户")
    @CostTime
    @PostMapping("/")
    public HttpResonse addUser(@RequestBody User user){
        if ("".equals(user.getUsername()) || "".equals(user.getPassword())) {
            return HttpResonse.fail().setMsg("用户名密码不能为空");
        }
        if (user.getUsername().length() < 6) {
            return HttpResonse.fail().setMsg("用户名必须长度大于6");
        }
        if (user.getPassword().length() < 6) {
            return HttpResonse.fail().setMsg("密码必须长度大于6");
        }
        if (userService.getOne(new QueryWrapper<User>()
                .eq("username",user.getUsername())) != null) {
            return HttpResonse.fail().setMsg("用户名已被使用");
        }
        Date date = new Date();
        user.setUserId(UUID.randomUUID().toString().trim().replaceAll("-",""));
        user.setCreateTime(date);
        user.setUpdateTime(date);
        if (userService.save(user) && userService.createMainFolder(user)){
            return HttpResonse.success().setMsg("注册成功");
        } else {
            return HttpResonse.fail().setMsg("注册失败");
        }
    }

    @ApiOperation("用户登陆")
    @PostMapping("/login")
    @CostTime
    public Map<String,Object> handleLogin(@RequestBody User user){
        Subject subject = SecurityUtils.getSubject();
        String token = JwtUtil.getJwtToken(user.getUsername(), user.getPassword(),"netdisk");   //toHex转换成16进制，32为字符
        JwtToken jwtToken = new JwtToken(token);
        Map<String,Object> res = new HashMap<>();
        try {
            subject.login(jwtToken);
            res.put("code",1);
            res.put("status",200);
            res.put("msg","登陆成功");
            User userIndDB = userService.getOne(new QueryWrapper<User>()
                    .eq("username",user.getUsername()));
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("user",userIndDB);
            tmp.put("token",token);
            res.put("data",tmp);
//            userService.updateLastLoginDate(date,user.getUsername());
        } catch (UnknownAccountException e){
            res.put("code",0);
            res.put("status",604);
            res.put("msg","无效用户，用户不存在");
        } catch (IncorrectCredentialsException e){
            res.put("code",0);
            res.put("status",603);
            res.put("msg","密码错误");
        } catch (ExpiredCredentialsException e){
            res.put("code",0);
            res.put("status",602);
            res.put("msg","token过期，请重新登录");
        } finally {
            return res;
        }
//        return res;
//        if (userIndDB == null){
//            return HttpResonse.fail().setMsg("用户不存在");
//        }
//        if (!userIndDB.getPassword().equals(user.getPassword())){
//            return HttpResonse.fail().setMsg("密码错误");
//        }
//        userIndDB.setPassword("");
//        return HttpResonse.success().setMsg("登陆成功").setData(userIndDB);
    }

}
