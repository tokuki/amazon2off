package jp.co.amazon2off.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Code {

    /**
     * ID
     */
    private Integer id;
    /**
     * 折扣码
     */
    private String code;
    /**
     * 领取优惠码的用户ID
     */
    private Integer userId;
    /**
     * 商品ID
     */
    private Integer listingId;
    /**
     * 活动开始时间
     */
    private Long startTime;
    /**
     * 活动结束时间
     */
    private Long endTime;
    /**
     * 添加时间
     */
    private Long addTime;
    /**
     * 领取时间
     */
    private Long receiveTime;
    /**
     * 状态
     */
    private Integer status;

}
