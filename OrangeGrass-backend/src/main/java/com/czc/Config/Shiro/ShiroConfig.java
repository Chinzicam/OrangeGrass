package com.czc.Config.Shiro;

import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@Configuration
public class ShiroConfig {
    @Autowired
    SecurityManager securityManager;

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("getDefaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();


        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //放行资源
        filterChainDefinitionMap.put("/api/nsfw/**", "anon");
        filterChainDefinitionMap.put("/api/advertise/video/**", "anon");
        filterChainDefinitionMap.put("/api/advertise/thumbnail/**", "anon");
        filterChainDefinitionMap.put("/api/websocket", "anon");
        filterChainDefinitionMap.put("/api/websocket/**", "anon");
        filterChainDefinitionMap.put("/api/user/", "anon");
        filterChainDefinitionMap.put("/api/user/login", "anon");
        filterChainDefinitionMap.put("/api/file/download/**", "anon");
        filterChainDefinitionMap.put("/api/file/chunkdownload/**", "anon");
        filterChainDefinitionMap.put("/api/avatar/**", "anon");
        filterChainDefinitionMap.put("/api/thumbnail/**", "anon");
        //受限资源
        filterChainDefinitionMap.put("/api/user", "jwt");
        filterChainDefinitionMap.put("/api/file/upload/chunk", "jwt");
        filterChainDefinitionMap.put("/api/file/chunkdownload/new", "jwt");
        filterChainDefinitionMap.put("/**", "jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        //配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        //shiroFilterFactoryBean.setLoginUrl("/api/v1/user/login");

        // 登录成功后要跳转的链接
        ///////gaidong
        //shiroFilterFactoryBean.setSuccessUrl("/api/v1/user/index");
        ///////

        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("getRealm") MyRealm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        //给安全管理器设置realm
        securityManager.setRealm(realm);

        //关闭shiro的session（无状态的方式使用shiro）
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public MyRealm getRealm() {
        MyRealm realm = new MyRealm();
        //修改凭证校验匹配器（处理加密），只有使用了UsernamePasswordToken并且有对password进行加密的才需要
//        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
//        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
//        hashedCredentialsMatcher.setHashIterations(1024);
//
//        realm.setCredentialsMatcher(hashedCredentialsMatcher);
        return realm;
    }

    /**
     * 开启shiro注解
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}



