package com.czc.Config.Shiro;

import com.czc.Util.JWT.JwtToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.czc.Constant.HttpResonse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {


    /**
     * 进行token的验证
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        //在请求头中获取token
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization"); //前端命名Authorization
//        System.out.println("token "+token);
        //token不存在
        if ("/api/user/".equals(((HttpServletRequest) request).getRequestURI())) {
            return true;
        }
        if(token == null || "".equals(token)){
            out(response, HttpResonse.fail().setMsg("无token，无权访问，请先登录"));
            return false;
        }

        //token存在，进行验证
        JwtToken jwtToken = new JwtToken(token);
        try {
            SecurityUtils.getSubject().login(jwtToken);  //通过subject，提交给myRealm进行登录验证
            return true;
        } catch (ExpiredCredentialsException e){
            out(response,HttpResonse.fail().setMsg("token过期，请重新登录").setStatus(777));
            e.printStackTrace();
            return false;
        } catch (ShiroException e){
            // 其他情况抛出的异常统一处理，由于先前是登录进去的了，所以都可以看成是token被伪造造成的
            out(response,HttpResonse.fail().setMsg("token被伪造，无效token").setStatus(777));
            e.printStackTrace();
            return false;
        }
    }

    /**
     * json形式返回结果token验证失败信息，无需转发
     */
    private void out(ServletResponse response, HttpResonse res) throws IOException {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        ObjectMapper mapper = new ObjectMapper();
        String jsonRes = mapper.writeValueAsString(res);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.getOutputStream().write(jsonRes.getBytes());
    }

    /**
     * 过滤器拦截请求的入口方法
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            return executeLogin(request, response);  //token验证
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * isAccessAllowed()方法返回false，即认证不通过时进入onAccessDenied方法
     */
//    @Override
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        return super.onAccessDenied(request, response);
//    }

    /**
     * token认证executeLogin成功后，进入此方法，可以进行token更新过期时间
     */
//    @Override
//    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {

//    }
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
