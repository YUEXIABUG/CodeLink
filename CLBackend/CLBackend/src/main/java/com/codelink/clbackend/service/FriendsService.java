package com.codelink.clbackend.service;

import com.codelink.clbackend.model.domain.Friends;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codelink.clbackend.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author yuexiabug
 * @description 针对表【friends(好友表)】的数据库操作Service
 * @createDate 2024-03-15 10:18:49
 */
public interface FriendsService extends IService<Friends> {

    /**
     * 获取所有好友uid
     * @param uid       用户id
     * @return 好友uid列表
     */
    List<User> getFriends(long uid, HttpServletRequest request);

    /**
     * 添加好友
     * @param uid       用户id
     * @param applyUserId 好友id
     * @return 是否添加成功
     */
    boolean addFriends(long uid, long applyUserId, HttpServletRequest request);

    /**
     * 删除好友
     * @param uid       用户id
     * @param friendUid 好友id
     * @return 是否删除成功
     */
    boolean deleteFriends(long uid, long friendUid, HttpServletRequest request);


}
