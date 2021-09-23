package jp.co.amazon2off.security.config;

import jp.co.amazon2off.security.realm.UserRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置
 */
@Slf4j
@Configuration
public class ShiroConfig {

    @Autowired
    LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        // 创建拦截链实例
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 设置拦截链map，拦截规则按顺序执行，匹配到，就停止匹配
        Map<String, String> linkedHashMap = new LinkedHashMap<>();

        // 创建将自己的jwt过滤器添加到shiro中
        HashMap<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("authc", new JwtFilter(true));
        shiroFilterFactoryBean.setFilters(filterMap);

        // 放行请求
        linkedHashMap.put("/api/user/login", "anon");
        linkedHashMap.put("/api/user/register", "anon");
        linkedHashMap.put("/api/listing/downloadImage", "anon");
        linkedHashMap.put("/api/listing/getListingList", "anon");
        linkedHashMap.put("/api/listing/getListingInfo", "anon");
        linkedHashMap.put("/api/listing/getListingListByCategory", "anon");
        linkedHashMap.put("/api/listing/getPopularListing", "anon");
        linkedHashMap.put("/api/Categorys/getCategorys", "anon");
        linkedHashMap.put("/filterError/*", "anon");
        //swagger配置放行
        linkedHashMap.put("/swagger-ui.html", "anon");
        linkedHashMap.put("/swagger/**", "anon");
        linkedHashMap.put("/webjars/**", "anon");
        linkedHashMap.put("/swagger-resources/**", "anon");
        linkedHashMap.put("/v3/**", "anon");
        linkedHashMap.put("/swagger-ui/**", "anon");
        // 拦截请求
        linkedHashMap.put("/**", "authc");
        // 配置拦截链到过滤器工厂
        shiroFilterFactoryBean.setFilterChainDefinitionMap(linkedHashMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm realm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        // 使用自己的realm
        defaultWebSecurityManager.setRealm(realm);
        /*
         * 关闭shiro自带的session，详情见文档
         * http://shiro.apache.org/session-management.html#SessionManagement-StatelessApplications%28Sessionless%29
         */
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        defaultWebSecurityManager.setSubjectDAO(subjectDAO);
        return defaultWebSecurityManager;
    }


//    @Bean
//    public UserRealm userRealm() {
//        UserRealm userRealm = new UserRealm();
//        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
//        return userRealm;
//    }

    /**
     * *
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * *
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    private HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 使用MD5算法
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        // 散列的次数
        hashedCredentialsMatcher.setHashIterations(1024);
        return hashedCredentialsMatcher;
    }
}
