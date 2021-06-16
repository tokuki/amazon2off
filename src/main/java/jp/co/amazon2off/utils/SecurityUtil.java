package jp.co.amazon2off.utils;

import jp.co.amazon2off.pojo.UserPojo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

public class SecurityUtil {
    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static UserPojo getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            throw new RuntimeException("Log current user error: UnAuthenticated subject");
        }
        return (UserPojo) subject.getPrincipal();
    }
}