package com.codelink.clbackend.service;

import com.codelink.clbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Array;
import java.util.List;

/**
* @author yuexiabug
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-01-04 09:52:27
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount       用户账号
     * @param userPassword      用户密码
     * @param checkPassword     两次密码一致
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount       用户账号
     * @param userPassword      用户密码
     * @param request           请求
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获得脱敏后的用户信息
     */
    User getHandleUser(User user);

    /**
     * 获取用户信息
     *
     * @param uid       用户id
     * @return 用户信息
     */
    User getUserInfo(long uid, HttpServletRequest request);

    /**
     * 获取用户登录状态
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 修改用户信息
     *
     * @param  uid               用户id
     * @param username       用户账号
     * @param userGender         用户性别
     * @param userAge            用户年龄
     * @param userPhone          用户手机号
     * @param userEmail          用户邮箱
     * @param github             github
     * @param personalWeb        个人博客
     * @param csdn               csdn
     * @return 修改后的用户信息
     */
    User updateUserInfo(long uid, String username, int userGender, int userAge, String userPhone, String userEmail, String github, String personalWeb, String csdn);

    /**
     * 修改用户标签
     *
     * @param uid       用户id
     * @param userTags   用户标签（数组）
     */
    User updateUserTags(long uid, String[] userTags,  HttpServletRequest request);

    /**
     * 推荐用户
     *
     * @return 推荐用户列表
     */
    List<User> recommendUsers(User loginUser, HttpServletRequest request);

    /**
     * 退出登录
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
