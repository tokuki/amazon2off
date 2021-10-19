package jp.co.amazon2off.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMailLog {
    /**
     * ID
     */
    private Integer id;

    /**
     * 邮件地址
     */
    private String mail;

    /**
     * code
     */
    private String code;

    /**
     * 类型 1-注册验证
     */
    private Integer type;

    /**
     * 添加时间
     */
    private Long addTime;
}
