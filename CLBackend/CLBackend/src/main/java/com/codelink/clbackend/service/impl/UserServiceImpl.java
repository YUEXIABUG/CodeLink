package com.codelink.clbackend.service.impl;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.mapper.FriendsMapper;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.UserService;
import com.codelink.clbackend.mapper.UserMapper;
import com.codelink.clbackend.utils.AlgorithmUtils;
import org.apache.commons.math3.util.Pair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户实现类
 *
 * @author yuexiabug
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-01-04 09:52:27
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private FriendsMapper friendsMapper;

    /**
     * 密码加密盐
     */
    public static final String SALT = "codelinkyouandme";

    /**
     *用户登录太键
     */
    public static final String USER_LOGIN_STATE = "userLoginState";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1、非空校验
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return -1;
        }
        // 2、校验账号或密码长度
        if(userAccount.length() < 6 || userAccount.length() > 20 || userPassword.length() < 8 || userPassword.length() > 20 || checkPassword.length() < 8 || checkPassword.length() > 20){
            return -1;
        }
        // 3、校验两次密码是否一致
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        // 4、校验账号是否包含特殊字符
        String VaildPattern = "^[a-zA-Z0-9_]+$";
        if(!userAccount.matches(VaildPattern)){
            return -1;
        }
        // 5、校验账号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            return -2;
        }
        // 6、密码加密
        String password = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        // 7、插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(password);
        boolean saveResult = this.save(user);
        if(!saveResult){
            return -1;
        }
        // 8、自动在friends表中创建一条记录
        long uid = user.getUid();
        SimpleDateFormat updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String createTime = updateTime.format(now);
        String friendsUid = "[]";
        friendsMapper.insertFriends(uid, friendsUid, createTime);
        return uid;
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、非空校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        // 2、校验账号或密码长度
        if(userAccount.length() < 6 || userAccount.length() > 20 || userPassword.length() < 8 || userPassword.length() > 20 ){
            return null;
        }
        // 3、校验账号是否包含特殊字符
        String VaildPattern = "^[a-zA-Z0-9_]+$";
        if(!userAccount.matches(VaildPattern)){
            return null;
        }
        // 4、密码加密
        String password = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        // 5、校验账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            return null;
        }
        // 6、校验密码是否正确
        QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userAccount", userAccount);
        queryWrapper1.eq("userPassword", password);
        User user1 = userMapper.selectOne(queryWrapper1);
        if(user1 == null){
            return null;
        }
        // 7、返回脱敏后的用户信息
        User handleUser = getHandleUser(user1);
        // 8、设置登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, handleUser);

        return handleUser;
    }

    @Override
    public User getHandleUser(User originalUser) {
        if (originalUser == null) {
            return null;
        }
        User handleUser = new User();
        handleUser.setUid(originalUser.getUid());
        handleUser.setUserAccount(originalUser.getUserAccount());
        handleUser.setUsername(originalUser.getUsername());
        handleUser.setUserAvatar(originalUser.getUserAvatar());
        handleUser.setUserGender(originalUser.getUserGender());
        return handleUser;
    }

    @Override
    public User getUserInfo(long uid, HttpServletRequest request) {
        // 1、检查登录状态
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }

        // 2、非空校验
        if(uid <= 0){
            return null;
        }
        // 3、校验用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            return null;
        }
        // 4、返回脱敏后的用户信息
        User userInfo = new User();
        userInfo.setUid(user.getUid());
        userInfo.setUserAccount(user.getUserAccount());
        userInfo.setUsername(user.getUsername());
        userInfo.setUserAvatar(user.getUserAvatar());
        userInfo.setUserGender(user.getUserGender());
        userInfo.setUserAge(user.getUserAge());
        userInfo.setUserTags(user.getUserTags());
        userInfo.setGithub(user.getGithub());
        userInfo.setPersonalWeb(user.getPersonalWeb());
        userInfo.setCsdn(user.getCsdn());
        userInfo.setUserEmail(user.getUserEmail());
        userInfo.setUserPhone(user.getUserPhone());
        userInfo.setPid(user.getPid());

        return userInfo;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1、检查登录状态
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj == null){
            return null;
        }

        // 获取 uid 为 userObj 的 uid 的用户的信息并返回
        User user = (User) userObj;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", user.getUid());
        User loginUser = userMapper.selectOne(queryWrapper);
        return loginUser;
    }

    @Override
    public User updateUserInfo(long uid, String username, int userGender, int userAge, String userPhone, String userEmail, String github, String personalWeb, String csdn) {
        // 1、非空校验
        if(uid <= 0){
            return null;
        }
        // 2、校验用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            return null;
        }
        // 3、修改用户信息
        user.setUsername(username);
        user.setUserGender(userGender);
        user.setUserAge(userAge);
        user.setUserPhone(userPhone);
        user.setUserEmail(userEmail);
        user.setGithub(github);
        user.setPersonalWeb(personalWeb);
        user.setCsdn(csdn);
        user.setUpdateTime(new Date());
        boolean updateResult = this.updateById(user);
        if(updateResult){
            return user;
        }else{
            return null;
        }
    }

    @Override
    public User updateUserTags(long uid, String[] userTags,  HttpServletRequest request) {
        // 1、检查登录状态
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、非空校验
        if(uid <= 0){
            return null;
        }
        // 3、校验用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            return null;
        }
        // 4、修改用户标签，首先要去重
        userTags = Arrays.stream(userTags).distinct().toArray(String[]::new);
        user.setUserTags(Arrays.toString(userTags));
        user.setUpdateTime(new Date());
        boolean updateResult = this.updateById(user);
        if(updateResult){
            return user;
        }else{
            return null;
        }
    }

    @Override
    public List<User> recommendUsers(User loginUser, HttpServletRequest request) {
        int num = 10;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("uid", "userTags");
        queryWrapper.isNotNull("userTags");
        queryWrapper.ne("userTags", "[]");
        List<User> userList = this.list(queryWrapper);
        // 查询好友表
        String friendsUid = friendsMapper.getFriends(loginUser.getUid());
        if (friendsUid == null) {
            friendsUid = "[]";
        }
        // 查询出来的结果是一个字符串，形如 '[1000001, 1000007]'，需要转换成List<Long>
        String[] friendsUidArray = friendsUid.split(",");
        // 将各个字符串中的'['、']'、' '去掉
        friendsUidArray[0] = friendsUidArray[0].substring(1);
        friendsUidArray[friendsUidArray.length - 1] = friendsUidArray[friendsUidArray.length - 1].substring(0, friendsUidArray[friendsUidArray.length - 1].length() - 1);
        for (int i = 0; i < friendsUidArray.length; i++) {
            friendsUidArray[i] = friendsUidArray[i].trim();
        }
        List<Long> friendsUidList = new ArrayList<>();
        for (String s : friendsUidArray) {
            if (StringUtils.isBlank(s)) {
                continue;
            }
            friendsUidList.add(Long.parseLong(s));
        }
        // 将好友排除在外
        userList = userList.stream().filter(user -> !friendsUidList.contains(user.getUid())).collect(Collectors.toList());

        String tags = loginUser.getUserTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 将标签转换成小写
        tagList = tagList.stream().map(String::toLowerCase).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(tagList)){
            return new ArrayList<>();
        }
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getUserTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || Objects.equals(user.getUid(), loginUser.getUid())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 将标签转换成小写
            userTagList = userTagList.stream().map(String::toLowerCase).collect(Collectors.toList());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getUid()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("uid", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            // 返回用户脱敏信息
            finalUserList.add(getUserInfo(userId, request));
        }
        return finalUserList;
    }


    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




