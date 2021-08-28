package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.UserMapper;
import jp.co.amazon2off.pojo.UserPojo;
import jp.co.amazon2off.utils.DateUtil;
import jp.co.amazon2off.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户邮箱查询用户信息
     *
     * @param userMail
     * @return
     */
    public UserPojo selectUserByUserMail(String userMail) {
        return userMapper.selectUserByUserMail(userMail);
    }

    /**
     * 保存用户信息
     *
     * @param userPojo
     * @return
     */
    public void saveUser(UserPojo userPojo) throws Exception {
        // 密码加密
        Md5Hash md5Hash = new Md5Hash(userPojo.getPassWord(), "", 1024);
        // 注册密码
        userPojo.setPassWordByMd5(md5Hash.toHex());
        // 注册时间
        userPojo.setRegisterTime(DateUtil.getCurrentTimeMillis());
        // 用户角色
        userPojo.setRoleId(userPojo.getRoleId());
        // 保存用户信息
        userMapper.saveUser(userPojo);
    }

    /**
     * 用户密码修改
     *
     * @param userPojo
     */
    public void updateUserPaw(UserPojo userPojo) throws Exception {
        // 密码加密
        Md5Hash md5Hash = new Md5Hash(userPojo.getPassWord(), "", 1024);
        userPojo.setPassWordByMd5(md5Hash.toHex());
        userMapper.updateUserPaw(userPojo);
        logout(SecurityUtils.getSubject());
    }

    /**
     * 获取用户个人信息
     *
     * @return
     */
    public UserPojo getUserInfo() throws Exception {
        log.info(SecurityUtil.getCurrentUser().getId().toString());
        return userMapper.getUserInfo(SecurityUtil.getCurrentUser().getId());
    }

    /**
     * 个人信息修改
     *
     * @param userPojo
     * @throws Exception
     */
    public void updateUserInfo(UserPojo userPojo) throws Exception {
        userPojo.setId(SecurityUtil.getCurrentUser().getId());
        userPojo.setUpdateTime(DateUtil.getCurrentTimeMillis());
        userMapper.updateUserInfo(userPojo);
    }

    /**
     * 用户退出
     *
     * @param subject
     * @throws Exception
     */
    public void logout(Subject subject) throws Exception {
        subject.logout();
//        if (!subject.isAuthenticated()) {
//            DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
//            UserRealm shiroRealm = (UserRealm) securityManager.getRealms().iterator().next();
//            shiroRealm.clearAllCache();
//        }
    }

    /**
     * 用户登陆
     *
     * @param subject
     * @throws Exception
     */
    public void login(Subject subject, UserPojo userPojo) throws Exception {
        subject.login(new UsernamePasswordToken(userPojo.getUserMail(), userPojo.getPassWord()));
    }
}
