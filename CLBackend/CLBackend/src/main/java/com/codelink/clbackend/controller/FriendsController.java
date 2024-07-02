package com.codelink.clbackend.controller;

import com.codelink.clbackend.DTO.friendApplicationDTO;
import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.model.domain.request.*;
import com.codelink.clbackend.service.FriendsApplicationService;
import com.codelink.clbackend.service.FriendsService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 好友接口
 */

@RestController
@RequestMapping("/friends")
public class FriendsController {

    @Resource
    FriendsService friendsService;
    @Resource
    FriendsApplicationService friendsApplicationService;

    @PostMapping("/getFriendsList")
    public BaseResponse<List<User>> getFriends(@RequestBody GetFriendsInfo getFriendsInfo, HttpServletRequest request) {
        if (getFriendsInfo == null) {
            return new BaseResponse<>(500, null, "获取好友请求为空");
        }

        long uid = getFriendsInfo.getUid();
        if (uid <= 0) {
            return new BaseResponse<>(400, null, "用户id为空");
        }

        List<User> friends = friendsService.getFriends(uid, request);
        if (friends == null) {
            return new BaseResponse<>(401, null, "获取好友失败");
        }

        return new BaseResponse<>(200, friends, "获取好友成功");
    }

    @PostMapping("/sendFriendsApplication")
    public BaseResponse<Long> addFriends(@RequestBody SendFriendsApplicationRequest sendFriendsApplicationRequest, HttpServletRequest request) {
        if (sendFriendsApplicationRequest == null) {
            return new BaseResponse<>(500, null, "添加好友请求为空");
        }

        long uid = sendFriendsApplicationRequest.getUid();
        long applyUserId = sendFriendsApplicationRequest.getApplyUserid();
        String remark = sendFriendsApplicationRequest.getRemark();
        if (uid <= 0 || applyUserId <= 0) {
            return new BaseResponse<>(400, null, "用户id为空");
        }

        long result = friendsApplicationService.addFriendsApplication(uid, applyUserId, remark, request);
        if (result <= 0) {
            return new BaseResponse<>(401, null, "添加好友失败");
        }

        return new BaseResponse<>(200, result, "添加好友成功");
    }

    @PostMapping("/setApplyStatus")
    public BaseResponse<Boolean> setApplyStatus(@RequestBody SetFriendsApplicationStatusRequest setFriendsApplicationStatusRequest, HttpServletRequest request) {
        if (setFriendsApplicationStatusRequest == null) {
            return new BaseResponse<>(500, null, "设置好友申请状态请求为空");
        }

        long aid = setFriendsApplicationStatusRequest.getAid();
        int applyStatus = setFriendsApplicationStatusRequest.getApplyStatus();
        if (aid <= 0) {
            return new BaseResponse<>(400, null, "申请id为空");
        }
        boolean result = friendsApplicationService.setApplyStatus(aid, applyStatus, request);
        if (!result) {
            return new BaseResponse<>(401, null, "设置好友申请状态失败");
        }

        return new BaseResponse<>(200, true, "设置好友申请状态成功");
    }

    @PostMapping("/addFriends")
    public BaseResponse<Boolean> addFriends(@RequestBody AddFriendsRequest addFriendsRequest, HttpServletRequest request) {
        if (addFriendsRequest == null) {
            return new BaseResponse<>(500, null, "添加好友请求为空");
        }

        long uid = addFriendsRequest.getUid();
        long applyUserId = addFriendsRequest.getApplyUserId();
        if (uid <= 0 || applyUserId <= 0) {
            return new BaseResponse<>(400, null, "用户id为空");
        }

        boolean result1 = friendsService.addFriends(uid, applyUserId, request);
        if (!result1) {
            return new BaseResponse<>(401, null, "添加好友失败");
        }

        boolean result2 = friendsService.addFriends(applyUserId, uid, request);
        if (!result2) {
            return new BaseResponse<>(401, null, "添加好友失败");
        }

        return new BaseResponse<>(200, true, "添加好友成功");
    }

    @PostMapping("/deleteFriends")
    public BaseResponse<Boolean> deleteFriends(@RequestBody DeleteFriendsRequest deleteFriendsRequest, HttpServletRequest request) {
        if (deleteFriendsRequest == null) {
            return new BaseResponse<>(500, null, "删除好友请求为空");
        }

        long uid = deleteFriendsRequest.getUid();
        long friendUid = deleteFriendsRequest.getFriendUid();
        if (uid <= 0 || friendUid <= 0) {
            return new BaseResponse<>(400, null, "用户id为空");
        }

        boolean result1 = friendsService.deleteFriends(uid, friendUid, request);
        if (!result1) {
            return new BaseResponse<>(401, null, "删除好友失败");
        }

        boolean result2 = friendsService.deleteFriends(friendUid, uid, request);
        if (!result2) {
            return new BaseResponse<>(401, null, "删除好友失败");
        }

        return new BaseResponse<>(200, true, "删除好友成功");
    }

    @PostMapping("/getFriendsApplicationFlag")
    public BaseResponse<Integer> getFriendsApplicationListFlag(@RequestBody GetFriednsApplyFlagRequest getFriednsApplyFlagRequest, HttpServletRequest request) {
        if (getFriednsApplyFlagRequest == null) {
            return new BaseResponse<>(500, null, "获取好友申请列表标记请求为空");
        }

        long uid = getFriednsApplyFlagRequest.getUid();
        if (uid <= 0) {
            return new BaseResponse<>(400, null, "用户id为空");
        }

        int result = friendsApplicationService.getFriendsApplicationListFlag(uid, request);
        return new BaseResponse<>(200, result, "获取好友申请列表标记成功");
    }

    @PostMapping("/getFriendsApplicationList")
    public BaseResponse<List<friendApplicationDTO>> getFriendsApplicationList(@RequestBody GetFriednsApplyFlagRequest getFriednsApplyFlagRequest, HttpServletRequest request) {
        if (getFriednsApplyFlagRequest == null) {
            return new BaseResponse<>(500, null, "获取好友申请列表请求为空");
        }

        long uid = getFriednsApplyFlagRequest.getUid();
        if (uid <= 0) {
            return new BaseResponse<>(400, null, "用户id为空");
        }

        List<friendApplicationDTO> result = friendsApplicationService.getFriendsApplicationList(uid, request);
        return new BaseResponse<>(200, result, "获取好友申请列表成功");
    }

    @PostMapping("/getRemark")
    public BaseResponse<String> getRemark(@RequestBody GetRemarkRequest getRemarkRequest, HttpServletRequest request) {
        if (getRemarkRequest == null) {
            return new BaseResponse<>(500, null, "获取好友申请备注请求为空");
        }

        long aid = getRemarkRequest.getAid();
        if (aid <= 0) {
            return new BaseResponse<>(400, null, "申请id为空");
        }

        String result = friendsApplicationService.getFriendsApplicationRemark(aid);
        return new BaseResponse<>(200, result, "获取好友申请备注成功");
    }
}