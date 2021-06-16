package jp.co.amazon2off.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Listing {
    /**
     * ID
     */
    private Integer id;
    /**
     * 商品Asin
     */
    private String asin;
    /**
     * 商品标题
     */
    private String title;
    /**
     * 商品原价格
     */
    private Double price;
    /**
     * 商品折后价格
     */
    private Double discountPrice;
    /**
     * 折扣百分比
     */
    private String discountPercentage;
    /**
     * 商品大图片URL
     */
    private String pictureURL;
    /**
     * 商品小图片URL
     */
    private String smallPictureURL;
    /**
     * 商品说明
     */
    private String explanation;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 商品品类
     */
    private Integer category;
    /**
     * 添加时间
     */
    private Long addTime;
    /**
     * 商品状态
     */
    private Integer status;
}
