package jp.co.amazon2off.mapper;

import jp.co.amazon2off.pojo.UserPojo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Mapper
@Repository
@Transactional
public interface UserMapper {

    /**
     * 用户注册
     *
     * @param userPojo
     * @return
     */
    void saveUser(@Param("user") UserPojo userPojo);

    /**
     * 根据用户邮箱查找用户信息
     *
     * @param userMail
     * @return
     */
    UserPojo selectUserByUserMail(@Param("userMail") String userMail);

    /**
     * 用户密码修改
     *
     * @param userPojo
     */
    void updateUserPaw(@Param("user") UserPojo userPojo);
}
