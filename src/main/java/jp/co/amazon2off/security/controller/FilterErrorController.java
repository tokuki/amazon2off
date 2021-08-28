package jp.co.amazon2off.security.controller;

import jp.co.amazon2off.utils.ResponseResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filterError")
public class FilterErrorController {

    @ResponseBody
    @RequestMapping("/{code}")
    public ResponseResult error(@PathVariable("code") String code) {
        return ResponseResult.error(code);
    }

}
