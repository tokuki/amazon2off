package jp.co.amazon2off.mapper;

import jp.co.amazon2off.pojo.CodePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
@Transactional
public interface CodeMapper {

    /**
     * 添加折扣码
     *
     * @param codeList
     * @param listingId
     * @param addTime
     * @param startTime
     * @param endTime
     */
    void addCode(@Param("codeList") List<String> codeList,
                 @Param("listingId") Integer listingId,
                 @Param("addTime") Long addTime,
                 @Param("startTime") Long startTime,
                 @Param("endTime") Long endTime);

    /**
     * 获取未开始活动的商品ID
     *
     * @param systemTime
     * @param startTime
     * @param endTime
     * @return
     */
    List<Integer> getListingIdByTime(@Param("systemTime") Long systemTime, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 获取被领取的商品ID
     *
     * @return
     */
    List<Integer> getListingIdByCode();

    /**
     * 领取优惠码
     *
     * @param codePojo
     */
    int updateCode(@Param("codePojo") CodePojo codePojo);

    /**
     * 根据用户ID和商品ID获取优惠码个数
     *
     * @param codePojo
     * @return
     */
    int codeNumByListingId(@Param("codePojo") CodePojo codePojo);

    /**
     * 根据用户获取活动时间
     *
     * @param userId
     * @return
     */
    List<CodePojo> getTimeByUserId(@Param("userId") Integer userId);

}
