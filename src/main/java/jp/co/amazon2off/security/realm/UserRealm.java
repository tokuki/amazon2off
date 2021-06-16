package jp.co.amazon2off.security.realm;

import jp.co.amazon2off.pojo.UserPojo;
import jp.co.amazon2off.service.RoleService;
import jp.co.amazon2off.service.UserService;
import jp.co.amazon2off.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义账户Realm
 */
@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

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
        return authorizationInfo;
    }

    /**
     * 认证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("--------------开始身份认证--------------");
        String userMail = (String) token.getPrincipal();
        UserPojo userPojo = userService.selectUserByUserMail(userMail);
        if (userPojo == null) {
            throw new UnknownAccountException();
        }
        if (userPojo.getStatus() == 0) {
            throw new DisabledAccountException();
        }
        return new SimpleAuthenticationInfo(userPojo, userPojo.getPassWord(), getName());
    }

}
