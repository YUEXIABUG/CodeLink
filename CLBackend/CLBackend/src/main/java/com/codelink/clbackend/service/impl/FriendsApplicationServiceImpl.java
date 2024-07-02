package com.codelink.clbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.DTO.friendApplicationDTO;
import com.codelink.clbackend.mapper.FriendsMapper;
import com.codelink.clbackend.model.domain.FriendsApplication;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.FriendsApplicationService;
import com.codelink.clbackend.mapper.FriendsApplicationMapper;
import com.codelink.clbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author yuexiabug
* @description 针对表【friends_application(好友申请表)】的数据库操作Service实现
* @createDate 2024-03-16 10:17:39
*/
@Service
public class FriendsApplicationServiceImpl extends ServiceImpl<FriendsApplicationMapper, FriendsApplication>
    implements FriendsApplicationService{

    @Resource FriendsApplicationMapper friendsApplicationMapper;
    @Resource FriendsMapper friendsMapper;
    @Resource UserService userService;

    @Override
    public long addFriendsApplication(long applyUserId, long uid, String remark,HttpServletRequest request) {
        // 1、进行非空校验
        if (uid <= 0 || applyUserId <= 0) {
            return 0;
        }
        // 2、查看是否已经是好友
        String friendsUid = friendsMapper.getFriends(uid);
        if (friendsUid == null) {
            return 0;
        }
        String[] friendsUidArray = friendsUid.split(",");
        friendsUidArray[0] = friendsUidArray[0].substring(1);
        friendsUidArray[friendsUidArray.length - 1] = friendsUidArray[friendsUidArray.length - 1].substring(0, friendsUidArray[friendsUidArray.length - 1].length() - 1);
        for (int i = 0; i < friendsUidArray.length; i++) {
            friendsUidArray[i] = friendsUidArray[i].trim();
        }

        // 3、添加好友申请
        FriendsApplication friendsApplication = new FriendsApplication();
        friendsApplication.setUid(uid);
        friendsApplication.setApplyUserId(applyUserId);
        friendsApplication.setRemark(remark);
        friendsApplication.setCreateTime(new Date());
        friendsApplicationMapper.insert(friendsApplication);

        return friendsApplication.getAid();
    }

    @Override
    public int getFriendsApplicationListFlag(long uid, HttpServletRequest request) {
        // 1、非空校验
        if (uid <= 0) {
            return 1;
        }
        // 2、查询好友申请列表标记，根据uid在friends_application表中查询，若有isRead为0的记录，则返回"有未读"，否则返回"无未读"
        int friendsApplicationListFlag = friendsApplicationMapper.getFriendsApplicationListFlag(uid);
        if (friendsApplicationListFlag > 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public List<friendApplicationDTO> getFriendsApplicationList(long uid, HttpServletRequest request) {
        // 1、非空校验
        if (uid <= 0) {
            return null;
        }
        // 2、查询好友申请列表，在friends_application表中查询所有uid的applyUserId
        List<FriendsApplication> applyUserId = friendsApplicationMapper.getFriendsApplicationList(uid);
        if (applyUserId == null) {
            return null;
        }
        // 将applyUserId根据createTime从新到旧排序
        applyUserId.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        // 3、将原friends_application表中的isRead置为1
        for (FriendsApplication friendsApplication : applyUserId) {
            friendsApplication.setIsRead(1);
            friendsApplicationMapper.updateById(friendsApplication);
        }

        // 4、根据好友申请列表中的applyUserId逐个查询好友信息，并添加applyStatus
        List<friendApplicationDTO> friendsApplicationList = new ArrayList<>();
        for (FriendsApplication friendsApplication : applyUserId) {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("uid", friendsApplication.getApplyUserId());
            User user = userService.getOne(userQueryWrapper);
            friendApplicationDTO friendApplicationDTO = new friendApplicationDTO();
            friendApplicationDTO.setUser(user);
            friendApplicationDTO.setUid(user.getUid());
            friendApplicationDTO.setUsername(user.getUsername());
            friendApplicationDTO.setUserAvatar(user.getUserAvatar());
            friendApplicationDTO.setUserTags(user.getUserTags());
            friendApplicationDTO.setApplyStatus(friendsApplication.getApplyStatus());
            friendApplicationDTO.setAid(friendsApplication.getAid());
            friendApplicationDTO.setRemark(friendsApplication.getRemark());
            friendsApplicationList.add(friendApplicationDTO);
        }

        // 5、返回好友申请列表
        return friendsApplicationList;
    }

    @Override
    public String getFriendsApplicationRemark(long aid) {
        // 1、非空校验
        if (aid <= 0) {
            return null;
        }
        // 2、根据aid查询好友申请remark
        FriendsApplication friendsApplication = friendsApplicationMapper.getFriendsApplication(aid);
        if (friendsApplication == null) {
            return null;
        }
        // 3、返回好友申请备注
        return friendsApplication.getRemark();
    }

    @Override
    public boolean setApplyStatus(long aid, int applyStatus, HttpServletRequest request) {
        // 1、非空校验
        if (aid <= 0) {
            return false;
        }
        // 2、根据uid和applyUserId查询好友申请
        FriendsApplication friendsApplication = friendsApplicationMapper.getFriendsApplication(aid);
        if (friendsApplication == null) {
            return false;
        }
        // 3、设置applyStatus
        friendsApplication.setApplyStatus(applyStatus);
        friendsApplicationMapper.updateById(friendsApplication);
        // 4、返回是否设置成功
        return true;
    }
}




