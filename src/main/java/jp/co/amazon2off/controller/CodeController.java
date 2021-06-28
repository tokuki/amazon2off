package jp.co.amazon2off.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jp.co.amazon2off.constant.ErrorCodeConstants;
import jp.co.amazon2off.service.CodeService;
import jp.co.amazon2off.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api
@RestController
@Slf4j
@RequestMapping("/api/code")
public class CodeController {

    @Autowired
    private CodeService codeService;

    @ApiOperation(value = "领取优惠码")
    @ApiImplicitParam(name = "listingId", value = "商品ID", required = true, paramType = "query", dataType = "int")
    @PostMapping("/receiveCode")
    @RequiresPermissions("buyer")
    public ResponseResult receiveCode(Integer listingId) {
        try {
            if (codeService.receiveCode(listingId) > 0) {
                return ResponseResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.C_0001);
    }

    @ApiOperation(value = "优惠码是否已领取")
    @ApiImplicitParam(name = "listingId", value = "商品ID", required = true, paramType = "query", dataType = "int")
    @PostMapping("/receiveCheck")
    @RequiresPermissions("buyer")
    public ResponseResult receiveCheck(Integer listingId) {
        try {
            return ResponseResult.success(codeService.receiveCheck(listingId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.C_0003);
    }

//    @ApiOperation(value = "优惠码一览")
//    @PostMapping("/receiveCodeList")
//    @RequiresPermissions("buyer")
//    public ResponseResult receiveCodeList() {
//        return ResponseResult.error(ErrorCodeConstants.C_0004);
//    }

    @ApiOperation(value = "商家发布优惠码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "listingId", value = "商品ID", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", required = true, paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", required = true, paramType = "query", dataType = "Long")
    })
    @PostMapping(value = "/addCode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresPermissions("seller")
    public ResponseResult addCode(Integer listingId, @RequestPart(value = "codeFile") MultipartFile codeFile, Long startTime, Long endTime) {
        try {
            codeService.addCode(listingId, codeFile, startTime, endTime);
            ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.C_0005);
    }

}
