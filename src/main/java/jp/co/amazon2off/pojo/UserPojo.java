package jp.co.amazon2off.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jp.co.amazon2off.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPojo extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 加密密码（MD5）
     */
    @JsonIgnore
    private String passWordByMd5;
    /**
     * 再次输入密码
     */
    @JsonIgnore
    private String passWordAgain;
    /**
     * 注册验证码
     */
    @JsonIgnore
    private String code;
}
