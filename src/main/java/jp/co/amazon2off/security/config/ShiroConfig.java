package jp.co.amazon2off.security.config;

import jp.co.amazon2off.security.realm.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro配置
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shirFilter() {
        //创建拦截链实例
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager());
        // 设置拦截链map，拦截规则按顺序执行，匹配到，就停止匹配
        Map<String, String> linkedHashMap = new LinkedHashMap<>();
        // 放行请求
        linkedHashMap.put("/**", "anon");
        // 配置拦截链到过滤器工厂
        shiroFilterFactoryBean.setFilterChainDefinitionMap(linkedHashMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        //创建默认的web安全管理器
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //配置shiro的自定义认证逻辑
        defaultWebSecurityManager.setRealm(userRealm());
        return defaultWebSecurityManager;
    }

    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return userRealm;
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
