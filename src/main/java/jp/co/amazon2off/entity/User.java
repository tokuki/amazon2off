package jp.co.amazon2off.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * ID
     */
    private Integer id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户邮箱
     */
    private String userMail;
    /**
     * 密码
     */
    private String passWord;
    /**
     * 注册时间
     */
    private Long registerTime;
    /**
     * 最后登录时间
     */
    private Long lastLoginTime;
    /**
     * 删除时间
     */
    private Long deleteTime;
    /**
     * 用户角色
     */
    private Integer roleId;
    /**
     * 状态
     */
    private Integer status;
}
