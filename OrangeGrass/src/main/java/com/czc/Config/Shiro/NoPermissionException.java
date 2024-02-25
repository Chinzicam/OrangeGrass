package com.czc.Config.Shiro;

import com.czc.Constant.HttpResonse;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class NoPermissionException {
    // 没有权限时抛出的异常
    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public HttpResonse handleShiroException(UnauthorizedException e) throws IOException {
        return HttpResonse.fail().setStatus(601).setMsg("无权限！");
    }

    // 权限校验失败时抛出的异常
    @ResponseBody
    @ExceptionHandler(AuthorizationException.class)
    public HttpResonse AuthorizationException(AuthorizationException e) throws IOException {
        return HttpResonse.fail().setStatus(602).setMsg("权限校验失败，请重新登录！");
    }

    @ResponseBody
    @ExceptionHandler(IncorrectCredentialsException.class)
    public HttpResonse IncorrectCredentialsException(IncorrectCredentialsException e) throws IOException {
        return HttpResonse.fail().setStatus(603).setMsg("密码错误！");
    }

    @ResponseBody
    @ExceptionHandler(UnknownAccountException.class)
    public HttpResonse UnknownAccountException(UnknownAccountException e) throws IOException {
        return HttpResonse.fail().setStatus(604).setMsg("账号不存在！");
    }

    @ResponseBody
    @ExceptionHandler(ExpiredCredentialsException.class)
    public HttpResonse ExpiredCredentialsException(ExpiredCredentialsException e) throws IOException {
        return HttpResonse.fail().setStatus(605).setMsg("token过期！");
    }
}
