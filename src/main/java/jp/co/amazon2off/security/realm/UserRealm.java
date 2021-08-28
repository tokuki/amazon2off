package jp.co.amazon2off.security.realm;

import jp.co.amazon2off.constant.ErrorCodeConstants;
import jp.co.amazon2off.pojo.UserPojo;
import jp.co.amazon2off.security.config.JwtToken;
import jp.co.amazon2off.service.RoleService;
import jp.co.amazon2off.service.UserService;
import jp.co.amazon2off.utils.JwtUtil;
import jp.co.amazon2off.utils.RedisUtil;
import jp.co.amazon2off.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;

/**
 * 自定义账户Realm
 */
@Slf4j
@Configuration
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Lazy
    @Resource
    private RedisUtil redisUtil;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("--------------开始授权--------------");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        String role = roleService.getRole(SecurityUtil.getCurrentUser().getRoleId());
        authorizationInfo.addStringPermission(role);
        log.info("--------------结束授权--------------");
        return authorizationInfo;
    }

    /**
     * 认证
     *
     * @param authToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
        log.info("--------------开始身份认证--------------");
        String token = (String) authToken.getPrincipal();
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException(ErrorCodeConstants.U_0015);
        }

        String userMail = JwtUtil.getUserMail(token);
        UserPojo userPojo = userService.selectUserByUserMail(userMail);
        if (userPojo == null) {
            throw new UnknownAccountException();
        }
        if (userPojo.getStatus() == 0) {
            throw new DisabledAccountException();
        }
        if (!jwtTokenRefresh(token, userPojo.getUserMail(), userPojo.getPassWord())) {
            throw new AuthenticationException(ErrorCodeConstants.U_0013);
        }
        log.info("--------------结束身份认证--------------");
        return new SimpleAuthenticationInfo(userPojo, token, getName());
    }

    /**
     * JWTToken刷新生命周期 （实现： 用户在线操作不掉线功能）
     *
     * @param userName
     * @param passWord
     * @return
     */
    public boolean jwtTokenRefresh(String token, String userName, String passWord) {
        String cacheToken = redisUtil.get(token);
        if (StringUtils.isNotEmpty(cacheToken)) {
            // 校验token有效性
            if (!JwtUtil.verify(cacheToken, userName, passWord)) {
                String newAuthorization = JwtUtil.sign(userName, passWord);
                // 设置超时时间
                redisUtil.setString(token, newAuthorization);
                redisUtil.expire(token, JwtUtil.EXPIRE_TIME * 2);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 清除当前用户的权限认证缓存
     *
     * @param principals 权限信息
     */
    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

}
