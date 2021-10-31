package jp.co.amazon2off.constant;

/**
 * 系统ERROR CODE常数类
 */
public class ErrorCodeConstants {

    /**
     * 用户相关ERROR CODE
     */
    // 注册失败
    public static String U_0001 = "U_0001";
    // 邮箱格式不正确
    public static String U_0002 = "U_0002";
    // 密码长度不足7位
    public static String U_0003 = "U_0003";
    // 邮箱不存在
    public static String U_0004 = "U_0004";
    // 密码错误
    public static String U_0005 = "U_0005";
    // 登陆失败
    public static String U_0006 = "U_0006";
    // 账户已被禁用
    public static String U_0007 = "U_0007";
    // 退出失败
    public static String U_0008 = "U_0008";
    // 用户信息修改失败
    public static String U_0009 = "U_0009";
    // 密码不一致
    public static String U_0010 = "U_0010";
    // 个人信息获取失败
    public static String U_0011 = "U_0011";
    // 个人信息修改失败
    public static String U_0012 = "U_0012";
    // token失效，重新登陆
    public static String U_0013 = "U_0013";
    // 注册邮件发送失败
    public static String U_0014 = "U_0014";
    // 未找到token，请登录
    public static String U_0015 = "U_0015";
    // 验证码已过期
    public static String U_0016 = "U_0016";
    // 验证码有误
    public static String U_0017 = "U_0017";
    // 两次密码不一致
    public static String U_0018 = "U_0018";
    // 邮箱已被注册
    public static String U_0019 = "U_0019";

    /**
     * 商品相关ERROR CODE
     */
    // 商品添加失败
    public static String L_0001 = "L_0001";
    // 商品查询失败
    public static String L_0002 = "L_0002";
    // 商品TOP信息获取失败
    public static String L_0003 = "L_0003";
    // 商品详情查询失败
    public static String L_0004 = "L_0004";
    // 商品图片上传失败
    public static String L_0005 = "L_0005";
    // 商品图片下载失败
    public static String l_0006 = "L_0006";
    // 商品主图片不能为空
    public static String l_0007 = "L_0007";
    // 卖家一览获取失败
    public static String l_0008 = "L_0008";
    // 普通用户一览获取失败
    public static String l_0009 = "l_0009";
    // 商家商品编辑失败
    public static String l_0010 = "l_0010";
    // 热门商品推荐获取失败
    public static String l_0011 = "l_0011";

    /**
     * Excel相关ERROR CODE
     */
    // Excel导出失败
    public static String EXC_0001 = "EXC_0001";

    /**
     * 优惠码相关ERROR CODE
     */
    // 领取优惠码失败
    public static String C_0001 = "C_0001";
    // 已领取过这个商品优惠码
    public static String C_0002 = "C_0002";
    // 优惠码check失败
    public static String C_0003 = "C_0003";
    // 优惠码一览获取失败
    public static String C_0004 = "C_0004";
    // 优惠码发布失败
    public static String C_0005 = "C_0005";

    /**
     * 商品类目相关ERROR CODE
     */
    // 商品类目失败
    public static String S_0001 = "S_0001";

}
