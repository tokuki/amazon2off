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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            @ApiImplicitParam(name = "pictureURL", value = "商品大图片URL", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "smallPictureURL", value = "商品小图片URL", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "explanation", value = "商品说明", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "category", value = "商品品类", required = true, paramType = "query", dataType = "String")
    })
    @PostMapping("/addListing")
    @RequiresPermissions("seller")
    public ResponseResult addListing(ListingPojo listingPojo, @RequestPart("multipartFile") MultipartFile multipartFile) {
        try {
            listingService.addListing(listingPojo, multipartFile);
            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.L_0001);
    }

    @ApiOperation(value = "商品模糊查询")
    @ApiImplicitParam(name = "keyWords", value = "搜索关键词", required = false, paramType = "query", dataType = "String")
    @GetMapping("/getListingList")
    public ResponseResult<List<ListingPojo>> getListingList(String keyWords) {
        try {
            return ResponseResult.success(listingService.getListingList(keyWords));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.L_0002);
    }

    @ApiOperation(value = "商品销量TOP")
    @GetMapping("/getListingTopInf")
    public ResponseResult<List<ListingPojo>> getListingTopInf() {
        try {
            return ResponseResult.success(listingService.getListingTopInf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.L_0003);
    }
}
