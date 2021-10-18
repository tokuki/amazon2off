package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.UserMapper;
import jp.co.amazon2off.pojo.UserPojo;
import jp.co.amazon2off.utils.DateUtil;
import jp.co.amazon2off.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 发送注册邮件
     *
     * @param mail
     */
    public void sendMail(String mail) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 参数
        StringBuilder params = new StringBuilder();
        try {
            // 字符数据最好encoding以下;这样一来，某些特殊字符才能传过去(如:某人的名字就是“&”,不encoding的话,传不过去)
            params.append("phone=").append(URLEncoder.encode("admin", "utf-8"));
            params.append("&");
            params.append("password=admin");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // 创建Post请求
        HttpPost httpPost = new HttpPost("http://localhost:12345/index.html/" + "?" + params);

        // 设置ContentType(注:如果只是传普通参数的话,ContentType不一定非要用application/json)
        httpPost.setHeader("Content-Type", "application/json;charset=utf8");

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getStatusLine());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
