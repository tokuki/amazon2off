package jp.co.amazon2off.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidationUtils implements Serializable {

    /**
     * 邮箱格式验证
     *
     * @param mail
     * @return
     */
    public static boolean mailOfValidation(String mail) {
        if (StringUtils.isNotBlank(mail)) {
            return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", mail);
        }
        return false;
    }

    /**
     * 电话格式验证
     *
     * @param phoneNumber
     * @return
     */
    public static boolean phoneNumberOfValidation(String phoneNumber) {
        if (StringUtils.isNotBlank(phoneNumber)) {
            return Pattern.matches("^(13[4,5,6,7,8,9]|15[0,8,9,1,7]|188|187)\\d{8}$", phoneNumber);
        }
        return false;
    }

    /**
     * 密码长度验证
     *
     * @param passWord
     * @return
     */
    public static boolean passWordOfValidation(String passWord) {
        if (StringUtils.isNotBlank(passWord)) {
            return passWord.length() >= 7;
        }
        return false;
    }

}
