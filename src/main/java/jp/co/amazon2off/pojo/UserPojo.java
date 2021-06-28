package jp.co.amazon2off.pojo;

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
    private String passWordByMd5;
    /**
     * 再次输入密码
     */
    private String passWordAgain;
}
