package jp.co.amazon2off.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jp.co.amazon2off.constant.ErrorCodeConstants;
import jp.co.amazon2off.pojo.ListingPojo;
import jp.co.amazon2off.service.ListingService;
import jp.co.amazon2off.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api
@RestController
@Slf4j
@RequestMapping("/api/listing")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @ApiOperation(value = "商品添加")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asin", value = "商品Asin", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "商品标题", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "price", value = "商品原价格", required = true, paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "discountPrice", value = "商品折后价格", required = true, paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "explanation", value = "商品说明", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "category", value = "商品品类", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", required = true, paramType = "query", dataType = "Long")
    })
    @PostMapping(value = "/addListing", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresPermissions("seller")
    public ResponseResult<Map<String, String>> addListing(ListingPojo listingPojo,
                                                          @RequestPart("coverImageFile") MultipartFile coverImageFile,
                                                          @RequestPart(value = "secondaryImageFile", required = false) MultipartFile[] secondaryImageFile,
                                                          @RequestPart(value = "codeFile") MultipartFile codeFile,
                                                          Long startTime, Long endTime) {
        try {
            if (coverImageFile.isEmpty()) {
                return ResponseResult.error(ErrorCodeConstants.l_0007);
            }
            return ResponseResult.success(listingService.addListing(listingPojo, coverImageFile, secondaryImageFile, codeFile, startTime, endTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.L_0001);
    }

    @ApiOperation(value = "商品图片下载")
    @GetMapping("downloadImage")
    public void downloadImage(String imageName, HttpServletResponse response) {
        try {
            listingService.downloadImage(imageName, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "商品模糊查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyWords", value = "搜索关键词", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "0-无作用，1-Free，2-50%以下优惠的商品，3-50%以上优惠的商品（不包括100%），4-近期要开始活动的商品，5-优惠码被领取最多的商品",
                    required = true, paramType = "query", dataType = "int")
    })
    @GetMapping("/getListingList")
    public ResponseResult<List<ListingPojo>> getListingList(ListingPojo listingPojo) {
        try {
            return ResponseResult.success(listingService.getListingList(listingPojo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.L_0002);
    }

    @ApiOperation(value = "商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "asin", value = "ASIN", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", required = true, paramType = "query", dataType = "Long")
    })
    @GetMapping("/getListingInfo")
    public ResponseResult<List<ListingPojo>> getListingInfo(ListingPojo listingPojo) {
        try {
            return ResponseResult.success(listingService.getListingInfo(listingPojo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.L_0004);
    }

    @ApiOperation(value = "根据品类获取商品列表")
    @ApiImplicitParam(name = "category", value = "商品品类", required = true, paramType = "query", dataType = "int")
    @GetMapping("/getListingListByCategory")
    public ResponseResult<List<ListingPojo>> getListingListByCategory(Integer category) {
        try {
            return ResponseResult.success(listingService.getListingListByCategory(category));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.L_0002);
    }

    @ApiOperation(value = "商家优惠卷一览&商品一览")
    @GetMapping(value = "/getSellerListingList")
    @RequiresPermissions("seller")
    public ResponseResult<ListingPojo> getSellerCodeList() {
        try {
            return ResponseResult.success(listingService.getSellerCodeList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.l_0008);
    }

    @ApiOperation(value = "普通用户优惠卷一览")
    @GetMapping(value = "/getBuyerCodeList")
    @RequiresPermissions("buyer")
    public ResponseResult<List<ListingPojo>> getBuyerCodeList() {
        try {
            return ResponseResult.success(listingService.getBuyerCodeList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.l_0009);
    }

    @ApiOperation(value = "商家商品编辑")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商品id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "asin", value = "商品Asin", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "商品标题", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "price", value = "商品原价格", required = false, paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "discountPrice", value = "商品折后价格(原价改动必传，否则不传)", required = false, paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "explanation", value = "商品说明", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "category", value = "商品品类", required = false, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", required = true, paramType = "query", dataType = "Long")
    })
    @PostMapping("/updateListingInfo")
    @RequiresPermissions("seller")
    public ResponseResult<Map<String, String>> updateListingInfo(ListingPojo listingPojo,
                                                                 @RequestPart(value = "coverImageFile", required = false) MultipartFile coverImageFile,
                                                                 @RequestPart(value = "secondaryImageFile", required = false) MultipartFile[] secondaryImageFile) {
        try {
            return ResponseResult.success(listingService.updateListingInfo(listingPojo, coverImageFile, secondaryImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.l_0010);
    }

    @ApiOperation(value = "热门商品推荐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "discountPercentage", value = "商品百分比数字", required = true, paramType = "query", dataType = "Double"),
            @ApiImplicitParam(name = "category", value = "商品品类", required = true, paramType = "query", dataType = "int")
    })
    @GetMapping("/getPopularListing")
    public ResponseResult<List<ListingPojo>> getPopularListing(ListingPojo listingPojo) {
        try {
            return ResponseResult.success(listingService.getPopularListing(listingPojo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.l_0011);
    }
}
