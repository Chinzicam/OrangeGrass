package com.czc.Config.aspect;

import com.czc.Service.SystemService;
import com.czc.Util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Aspect
@Component
@Slf4j
public class CostTimeAspect {
    //定义切点，拦截使用了@CostTime注解的方法
    @Pointcut(value = "@annotation(com.czc.Config.annotation.CostTime)")
    public void costTime() {
    }

    @Autowired
    private SystemService systemService;

    //环绕通知，记录方法执行时间
    @Around("costTime()")
    public Object costTimeAround(ProceedingJoinPoint joinPoint) {
        //获取请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object obj = null;
        try {
            //记录开始时间
            long beginTime = System.currentTimeMillis();
            //获取当前时间
            Date date = new Date();
            obj = joinPoint.proceed();
            //计算耗时
            long cost = System.currentTimeMillis() - beginTime;
            //获取ip
            String ip = IPUtil.getIpAddr(request);
            //获取方法名称
            String method = joinPoint.getSignature().getName();
            //获取uri
            String uri = request.getRequestURI();
            //获取类名称
            String className=joinPoint.getSignature().getDeclaringTypeName();
            //记录接口访问记录
            systemService.addInterfaceAccessRecord(uri,className,method,cost,date,ip);
            //记录日志
            log.info("URI:[{}]，IP:[{}]，类:[{}]，方法:[{}]，接口耗时:[{}]",uri,ip,className,method, cost + "毫秒");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return obj;
    }
}
