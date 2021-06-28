package jp.co.amazon2off.pojo;

import jp.co.amazon2off.entity.Listing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListingPojo extends Listing implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 0 - 无作用
     * 1 - Free
     * 2 - 50%以下优惠的商品
     * 3 - 50%以上优惠的商品（不包括100%）
     * 4 - 近期要开始活动的商品（2天）
     * 5 - 优惠码被领取最多的商品
     */
    private int type;

    /**
     * 搜索关键词
     */
    private String keyWords;
    /**
     * 折扣码
     */
    private String code;

    /**
     * 已被取数量
     */
    private int allNum;

    /**
     * 优惠码数量
     */
    private int receivedNum;
    /**
     * 活动开始时间
     */
    private Long startTime;
    /**
     * 活动结束时间
     */
    private Long endTime;
}
