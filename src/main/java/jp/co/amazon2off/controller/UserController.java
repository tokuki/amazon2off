package jp.co.amazon2off.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jp.co.amazon2off.constant.ErrorCodeConstants;
import jp.co.amazon2off.pojo.UserPojo;
import jp.co.amazon2off.security.realm.UserRealm;
import jp.co.amazon2off.service.UserService;
import jp.co.amazon2off.utils.ResponseResult;
import jp.co.amazon2off.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api
@RestController
@Slf4j
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userMail", value = "用户邮箱", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "passWord", value = "用户密码", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "roleId", value = "用户角色", required = true, paramType = "query", dataType = "int")
    })
    @PostMapping("/register")
    public ResponseResult register(UserPojo userPojo) {
        try {
            if (!ValidationUtils.mailOfValidation(userPojo.getUserMail())) {
                return ResponseResult.error(ErrorCodeConstants.U_0002);
            }
            if (!ValidationUtils.passWordOfValidation(userPojo.getPassWord())) {
                return ResponseResult.error(ErrorCodeConstants.U_0003);
            }
            userService.saveUser(userPojo);
            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.U_0001);
    }

    @ApiOperation(value = "用户登陆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userMail", value = "用户邮箱", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "passWord", value = "用户密码", required = true, paramType = "query", dataType = "String")
    })
    @PostMapping("/login")
    public ResponseResult login(UserPojo userPojo) {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(new UsernamePasswordToken(userPojo.getUserMail(), userPojo.getPassWord()));
            if (subject.isAuthenticated()) {
                return ResponseResult.success();
            }
        } catch (UnknownAccountException e) {
            e.printStackTrace();
            return ResponseResult.error(ErrorCodeConstants.U_0004);
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            return ResponseResult.error(ErrorCodeConstants.U_0005);
        } catch (DisabledAccountException e) {
            e.printStackTrace();
            return ResponseResult.error(ErrorCodeConstants.U_0007);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.U_0006);
    }

    @ApiOperation(value = "退出登陆")
    @GetMapping("/logout")
    @RequiresPermissions(value = {"seller", "buyer"}, logical = Logical.OR)
    public ResponseResult logout() {
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
            if (!subject.isAuthenticated()) {
                DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
                UserRealm shiroRealm = (UserRealm) securityManager.getRealms().iterator().next();
                shiroRealm.clearAllCache();
                return ResponseResult.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.U_0008);
    }

    @ApiOperation(value = "用户密码修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userMail", value = "用户邮箱", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "passWord", value = "用户密码", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "passWordAgain", value = "再次输入用户密码", required = true, paramType = "query", dataType = "String")
    })
    @PostMapping("/updateUserPaw")
    @RequiresPermissions(value = {"seller", "buyer"}, logical = Logical.OR)
    public ResponseResult updateUserPaw(UserPojo userPojo) {
        try {
            if (!userPojo.getPassWord().equals(userPojo.getPassWordAgain())) {
                return ResponseResult.error(ErrorCodeConstants.U_0010);
            }
            userService.updateUserPaw(userPojo);
            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.U_0009);
    }

    @ApiOperation(value = "用户登陆状态")
    @PostMapping("/loginState")
    @RequiresPermissions(value = {"seller", "buyer"}, logical = Logical.OR)
    public ResponseResult loginState() {
        return ResponseResult.success(SecurityUtils.getSubject().isAuthenticated());
    }

    @ApiOperation(value = "获取用户个人信息")
    @GetMapping("/getUserInfo")
    @RequiresPermissions(value = {"seller", "buyer"}, logical = Logical.OR)
    public ResponseResult<UserPojo> getUserInfo() {
        try {
            return ResponseResult.success(userService.getUserInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.U_0011);
    }

    @ApiOperation(value = "用户个人信息修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gender", value = "性别", required = false, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "birthday", value = "生日", required = false, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "intro", value = "个人介绍", required = false, paramType = "query", dataType = "String")
    })
    @PostMapping("/updateUserInfo")
    public ResponseResult updateUserInfo(UserPojo userPojo) {
        try {
            userService.updateUserInfo(userPojo);
            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.error(ErrorCodeConstants.U_0012);
    }

}
