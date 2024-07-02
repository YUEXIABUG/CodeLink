package com.codelink.clbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.mapper.FriendsApplicationMapper;
import com.codelink.clbackend.mapper.FriendsMapper;
import com.codelink.clbackend.model.domain.Friends;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.FriendsService;
import com.codelink.clbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yuexiabug
 * @description 针对表【friends(好友表)】的数据库操作Service实现
 * @createDate 2024-03-15 10:18:49
 */
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends> implements FriendsService {

    @Resource FriendsMapper friendsMapper;
    @Resource FriendsApplicationMapper friendsApplicationMapper;
    @Resource UserService userService;

    @Override
    public List<User> getFriends(long uid, HttpServletRequest request) {
        // 1、非空校验
        if (uid <= 0) {
            return null;
        }
        // 2、查询好友列表
        String friendsUid = friendsMapper.getFriends(uid);
        if (friendsUid == null) {
            friendsUid = "[]";
        }
        // 3、查询出来的结果是一个字符串，形如 '[1000001, 1000007]'，需要转换成List<Long>
        String[] friendsUidArray = friendsUid.split(",");
        // 将各个字符串中的'['、']'、' '去掉
        friendsUidArray[0] = friendsUidArray[0].substring(1);
        friendsUidArray[friendsUidArray.length - 1] = friendsUidArray[friendsUidArray.length - 1].substring(0, friendsUidArray[friendsUidArray.length - 1].length() - 1);
        for (int i = 0; i < friendsUidArray.length; i++) {
            friendsUidArray[i] = friendsUidArray[i].trim();
        }
        List<Long> friendsUidList = new ArrayList<>();
        for (String s : friendsUidArray) {
            friendsUidList.add(Long.parseLong(s));
        }
        // 4、根据好友uid列表逐个查询好友信息
        List<User> friends = new ArrayList<>();
        for (Long friendUid : friendsUidList) {
            User friend = userService.getUserInfo(friendUid, request);
            if (friend != null) {
                friends.add(friend);
            }
        }
        // 6、返回好友列表
        return friends;
    }

    @Override
    public boolean addFriends(long uid, long applyUserId, HttpServletRequest request) {
        // 1、非空校验
        if (uid <= 0 || applyUserId <= 0) {
            return false;
        }
        // 2、查询好友列表，若为空，则设置为'[]'
        String friendsUid = friendsMapper.getFriends(uid);
        if (friendsUid == null) {
            friendsUid = "[]";
        }
        // 3、查询出来的结果是一个字符串，形如 '[1000001, 1000007]'，需要转换成List<Long>
        String[] friendsUidArray = friendsUid.split(",");
        // 将各个字符串中的'['、']'、' '去掉
        friendsUidArray[0] = friendsUidArray[0].substring(1);
        friendsUidArray[friendsUidArray.length - 1] = friendsUidArray[friendsUidArray.length - 1].substring(0, friendsUidArray[friendsUidArray.length - 1].length() - 1);
        for (int i = 0; i < friendsUidArray.length; i++) {
            friendsUidArray[i] = friendsUidArray[i].trim();
        }

        List<Long> friendsUidList = new ArrayList<>();
        if (!friendsUidArray[0].equals("")) {
            for (String s : friendsUidArray) {
                friendsUidList.add(Long.parseLong(s));
            }
        }
        // 4、添加好友
        if (friendsUidList.contains(applyUserId)) {
            return false;
        }
        friendsUidList.add(applyUserId);
        // 5、将一整个newFriendsUidList转换成字符串
        String newFriendsUid = friendsUidList.toString();
        // 6、自动生成一个时间戳，形如 '2024-03-15 10:18:49'
        SimpleDateFormat updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        // 格式化日期时间
        String createTime = updateTime.format(now);
        // 7、更新数据库
        boolean result = friendsMapper.updateFriends(uid, newFriendsUid, createTime);
        return result;
    }

    @Override
    public boolean deleteFriends(long uid, long friendUid, HttpServletRequest request) {
        // 1、非空校验
        if (uid <= 0 || friendUid <= 0) {
            return false;
        }
        // 2、查询好友列表
        String friendsUid = friendsMapper.getFriends(uid);
        if (friendsUid == null) {
            return false;
        }
        // 3、查询出来的结果是一个字符串，形如 '[1000001, 1000007]'，需要转换成List<Long>
        String[] friendsUidArray = friendsUid.split(",");
        // 将各个字符串中的'['、']'、' '去掉
        friendsUidArray[0] = friendsUidArray[0].substring(1);
        friendsUidArray[friendsUidArray.length - 1] = friendsUidArray[friendsUidArray.length - 1].substring(0, friendsUidArray[friendsUidArray.length - 1].length() - 1);
        for (int i = 0; i < friendsUidArray.length; i++) {
            friendsUidArray[i] = friendsUidArray[i].trim();
        }

        List<Long> friendsUidList = new ArrayList<>();
        for (String s : friendsUidArray) {
            friendsUidList.add(Long.parseLong(s));
        }
        // 4、删除好友
        if (!friendsUidList.contains(friendUid)) {
            return false;
        }
        friendsUidList.remove(friendUid);
        // 5、将一整个newFriendsUidList转换成字符串
        String newFriendsUid = friendsUidList.toString();
        // 6、自动生成一个时间戳，形如 '2024-03-15 10:18:49'
        SimpleDateFormat updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        // 格式化日期时间
        String createTime = updateTime.format(now);
        // 7、更新数据库
        boolean result = friendsMapper.updateFriends(uid, newFriendsUid, createTime);

        return result;
    }
}




