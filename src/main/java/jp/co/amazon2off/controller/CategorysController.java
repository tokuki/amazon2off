package jp.co.amazon2off.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jp.co.amazon2off.constant.ErrorCodeConstants;
import jp.co.amazon2off.service.CategorysService;
import jp.co.amazon2off.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@Slf4j
@RequestMapping("/api/Categorys")
public class CategorysController {

    @Autowired
    private CategorysService categorysService;

    @ApiOperation(value = "获取商品类目")
    @GetMapping("/getCategorys")
    public ResponseResult getCategorys() {
        try {
            return ResponseResult.success(categorysService.getCategorys());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.S_0001);
    }

}
