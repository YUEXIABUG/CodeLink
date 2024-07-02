package com.codelink.clbackend.service;

import com.codelink.clbackend.DTO.friendApplicationDTO;
import com.codelink.clbackend.model.domain.FriendsApplication;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【friends_application(好友申请表)】的数据库操作Service
* @createDate 2024-03-16 10:17:39
*/
public interface FriendsApplicationService extends IService<FriendsApplication> {

    /**
     * 添加好友申请
     * @param uid                用户uid
     * @param applyUserId       申请人uid
     * @param remark            备注
     * @return 申请id
     */
    long addFriendsApplication(long applyUserId, long uid, String remark,HttpServletRequest request);

    /**
     * 获取好友申请列表标记
     * @param uid       用户uid
     * @return 好友申请列表标记
     */
    int getFriendsApplicationListFlag(long uid, HttpServletRequest request);

    /**
     * 获取好友申请列表
     * @param uid       用户uid
     * @return 好友申请列表
     */
    List<friendApplicationDTO> getFriendsApplicationList(long uid, HttpServletRequest request);

    /**
     * 获取好友申请备注
     * @param aid      申请id
     * @return 好友申请备注
     */
    String getFriendsApplicationRemark(long aid);

    /**
     * 设置applyStatus
     * @param aid            申请id
     * @param applyStatus    申请状态
     * @return 是否设置成功
     */
    boolean setApplyStatus(long aid, int applyStatus, HttpServletRequest request);
}
