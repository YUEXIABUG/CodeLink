package com.codelink.clbackend.controller;

import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.common.ResultUtils;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.model.domain.request.*;
import com.codelink.clbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;

import java.util.List;


/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return new BaseResponse<>(500, null, "注册请求为空");
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return new BaseResponse<>(400, null, "账号或密码为空");
        }

        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        if (result == -1) {
            return new BaseResponse<>(401, null, "注册失败");
        }
        if (result == -2) {
            return new BaseResponse<>(402, null, "账号已存在");
        }

        return new BaseResponse<>(200, result, "注册成功");
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            // 抛出异常
            return new BaseResponse<>(500, null, "登录请求为空");
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            // 抛出异常
            return new BaseResponse<>(501, null, "账号或密码为空");
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        if (user == null) {
            return new BaseResponse<>(401, null, "账号或密码错误");
        }

        return new BaseResponse<>(200, user, "登录成功");
    }

    @PostMapping("/getUserInfo")
    public BaseResponse<User> getUserInfo(@RequestBody GetUserInfoRequest getUserInfoRequest, HttpServletRequest request) {
        if (getUserInfoRequest == null) {
            return new BaseResponse<>(500, null, "获取用户信息请求为空");
        }

        long uid = getUserInfoRequest.getUid();

        User user = userService.getUserInfo(uid, request);
        if (user == null) {
            return new BaseResponse<>(400, null, "获取用户信息失败");
        }

        return new BaseResponse<>(200, user, "获取用户信息成功");
    }

    @PostMapping("/updateUserInfo")
    public BaseResponse<User> updateUserInfo(@RequestBody UpdateUserInfo updateUserInfo) {
        if (updateUserInfo == null) {
            return new BaseResponse<>(401, null, "修改用户信息请求为空");
        }

        long uid = updateUserInfo.getUid();
        String username = updateUserInfo.getUsername();
        int userGender = updateUserInfo.getUserGender();
        int userAge = updateUserInfo.getUserAge();
        String userPhone = updateUserInfo.getUserPhone();
        String userEmail = updateUserInfo.getUserEmail();
        String github = updateUserInfo.getGithub();
        String personalWeb = updateUserInfo.getPersonalWeb();
        String csdn = updateUserInfo.getCsdn();

        User user = userService.updateUserInfo(uid, username, userGender, userAge, userPhone, userEmail, github, personalWeb, csdn);
        if (user == null) {
            return new BaseResponse<>(400, null, "修改用户信息失败");
        }

        return new BaseResponse<>(200, user, "修改用户信息成功");
    }

    @PostMapping("/updateUserTags")
    public BaseResponse<User> updateUserTags(@RequestBody UpdateUserTags updateUserTags,  HttpServletRequest request) {
        if (updateUserTags == null) {
            return new BaseResponse<>(401, null, "修改用户标签请求为空");
        }

        long uid = updateUserTags.getUid();
        String[] userTags = updateUserTags.getUserTags();

        User user = userService.updateUserTags(uid, userTags, request);
        if (user == null) {
            return new BaseResponse<>(400, null, "修改用户标签失败");
        }

        return new BaseResponse<>(200, user, "修改用户标签成功");
    }

    @PostMapping("/recommendUsers")
    public BaseResponse<List<User>> recommendUsers(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<User> recommendUsers = userService.recommendUsers(loginUser, request);
        return ResultUtils.success(recommendUsers);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        int result = userService.userLogout(request);
        if (result == 0) {
            return new BaseResponse<>(400, null, "退出登录失败");
        }

        return new BaseResponse<>(200, result, "退出登录成功");
    }
}
