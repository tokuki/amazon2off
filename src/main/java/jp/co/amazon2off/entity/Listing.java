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
     * 商品主图大图片
     */
    private String coverImage;
    /**
     * 商品主图小图片
     */
    private String smallCoverImage;
    /**
     * 商品副图大图a
     */
    private String secondaryImageA;
    /**
     * 商品副图小图a
     */
    private String smallSecondaryImageA;
    /**
     * 商品副图大图b
     */
    private String secondaryImageB;
    /**
     * 商品副图小图b
     */
    private String smallSecondaryImageB;
    /**
     * 商品副图大图c
     */
    private String secondaryImageC;
    /**
     * 商品副图小图c
     */
    private String smallSecondaryImageC;
    /**
     * 商品副图大图d
     */
    private String secondaryImageD;
    /**
     * 商品副图小图d
     */
    private String smallSecondaryImageD;
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
     * 更新时间
     */
    private Long updateTime;
    /**
     * 商品状态
     */
    private Integer status;
}
