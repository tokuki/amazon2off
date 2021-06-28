package jp.co.amazon2off.mapper;

import jp.co.amazon2off.pojo.ListingPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Mapper
@Repository
@Transactional
public interface ListingMapper {

    /**
     * 商品添加
     *
     * @param listingPojo
     */
    void addListing(@Param("listing") ListingPojo listingPojo);

    /**
     * 商品查询
     *
     * @param keyWords
     * @return
     */
    List<ListingPojo> getListByKeyWords(@Param("keyWords") String keyWords);

    /**
     * 根据商品ID查找商品
     *
     * @param listingIds
     * @return
     */
    List<ListingPojo> getListByListingId(@Param("listingIds") List<Integer> listingIds);

    /**
     * 根据优惠力度查询商品
     *
     * @param listingPojo
     * @return
     */
    List<ListingPojo> getListByDiscount(@Param("listingPojo") ListingPojo listingPojo);

    /**
     * 商品相亲查询
     *
     * @param listingPojo
     * @return
     */
    List<ListingPojo> getListingInfo(@Param("listingPojo") ListingPojo listingPojo);

    /**
     * 根据品类获取商品列表
     *
     * @param category
     * @return
     */
    List<ListingPojo> getListingListByCategory(@Param("category") Integer category);

    /**
     * 商家优惠卷一览&商品一览
     *
     * @return
     */
    List<ListingPojo> getSellerCodeList(@Param("userId") Integer userId);

    /**
     * 普通用户优惠卷一览
     *
     * @param userId
     * @return
     */
    List<ListingPojo> getBuyerCodeList(@Param("userId") Integer userId);

    /**
     * 商家商品编辑
     *
     * @param listingPojo
     */
    void updateListingInfo(@Param("listingPojo") ListingPojo listingPojo);

    /**
     * 热门推荐(根据百分比)
     *
     * @param listingPojo
     * @return
     */
    List<ListingPojo> getPopularListingByPer(@Param("listingPojo") ListingPojo listingPojo);

    /**
     * 热门推荐(根据优惠码领取)
     *
     * @param listingPojo
     * @return
     */
    List<ListingPojo> getPopularListingByCode(@Param("listingPojo") ListingPojo listingPojo);
}
