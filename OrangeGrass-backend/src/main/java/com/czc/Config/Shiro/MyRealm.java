package com.czc.Config.Shiro;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.czc.Entity.User;
import com.czc.Util.JWT.JwtToken;
import com.czc.Util.JWT.JwtUtil;
import com.czc.Service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

/**
 * realm类
 */
public class MyRealm extends AuthorizingRealm {

    @Resource
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("进入鉴权");
        // 获取当前登录用户
        Subject subject = SecurityUtils.getSubject();
        User user = new User();
        System.out.println(subject.getPrincipal().toString());
        user=(User)subject.getPrincipal();
        System.out.println("鉴权中username："+user.getUsername());
        // 获取 SimpleAuthorizationInfo 对象写入授权规则
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 创建一个 set 集合用来保存当前用户的授权信息
        Set<String> role = new HashSet<>();
        role.add(userService.isAdmin(user.getUserId()) ? "admin" : "user");
        info.setRoles(role);
        // 将授权信息写入 SimpleAuthorizationInfo 对象中
        return info;
    }

    /**
     * 认证
     * @param
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) {
        String token = (String) auth.getCredentials();  //JwtToken中重写了这个方法了
        String username = JwtUtil.getUsername(token);   // 获得username
        DecodedJWT decodedJWT = JWT.decode(token);
        String Password = decodedJWT.getClaim("password").asString();
        //用户不存在（这个在登录时不会进入，只有在token校验时才有可能进入）
        if(username == null) {
            throw new UnknownAccountException();
        }
        //根据用户名，查询数据库获取到正确的用户信息
        User user = userService.getOne(new QueryWrapper<User>()
                .eq("username",username));

        //用户不存在（这个在登录时不会进入，只有在token校验时才有可能进入）
        if(user == null) {
            throw new UnknownAccountException();
        }
        //密码错误(这里获取到password，就是3件套处理后的保存到数据库中的凭证，作为密钥)
        if (!Password.equals(user.getPassword())) {
            throw new IncorrectCredentialsException();
        }

        return new SimpleAuthenticationInfo(user, token, getName());
    }
}
