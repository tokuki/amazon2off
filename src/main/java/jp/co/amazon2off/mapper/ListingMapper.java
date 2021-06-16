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
    List<ListingPojo> getListingList(@Param("keyWords") String keyWords);

    /**
     * @return
     */
    List<ListingPojo> getListingTopId();

    /**
     * 商品销量TOP
     *
     * @param listingPojo
     * @return
     */
    List<ListingPojo> getListingTopInf(@Param("listingPojo") List<ListingPojo> listingPojo);

}
