package jp.co.amazon2off.service;

import com.alibaba.fastjson.JSONObject;
import jp.co.amazon2off.constant.Constants;
import jp.co.amazon2off.mapper.UserMapper;
import jp.co.amazon2off.pojo.SendMailLogPojo;
import jp.co.amazon2off.pojo.UserPojo;
import jp.co.amazon2off.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtil redisUtil;

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

    /**
     * 发送注册邮件
     *
     * @param mail
     */
    public void sendMail(String mail) throws Exception {
        StringBuffer code = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            code.append(new Random().nextInt(9));
        }

        Map<String, Object> params = new HashMap<>();
        params.put("apiUser", Constants.API_USER);
        params.put("apiKey", Constants.API_KEY);
        params.put("from", Constants.FROM_MAIL);
        params.put("to", mail);
        params.put("subject", Constants.SUBJECT);
        params.put("html", code.toString());

        String json = HttpClientUtil.httpPostRequest(Constants.SEND_MAIL_URL, params);

        Map<String, Object> map = FastJsonUtil.stringToMap(json);
        if ("200".equals(map.get("statusCode").toString())) {
            String ciphertext = SignUtil.encrypt(mail, Constants.KEY_REGISTER_CODE);

            redisUtil.setString(ciphertext, code.toString(), 300);

            SendMailLogPojo sendMailLogPojo = new SendMailLogPojo();
            sendMailLogPojo.setMail(mail);
            sendMailLogPojo.setCode(code.toString());
            sendMailLogPojo.setType(1);
            sendMailLogPojo.setAddTime(DateUtil.getCurrentTimeMillis());

            userMapper.saveSendMailLog(sendMailLogPojo);
        }
    }
}
