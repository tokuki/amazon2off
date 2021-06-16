package jp.co.amazon2off.service;

import jp.co.amazon2off.mapper.UserMapper;
import jp.co.amazon2off.pojo.UserPojo;
import jp.co.amazon2off.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
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
        userPojo.setRegisterTime(DateUtils.getCurrentTimeMillis());
        // 用户角色
        userPojo.setRoleId(3);
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
    }
}
