package com.codelink.clbackend.controller;

import com.codelink.clbackend.DTO.teamMessagesListDTO;
import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.model.domain.request.AgreeJoinApplyRequest;
import com.codelink.clbackend.model.domain.request.ApplyJoinTeamRequest;
import com.codelink.clbackend.model.domain.request.GetTeamMessagesRequest;
import com.codelink.clbackend.model.domain.request.RejectJoinApplyRequest;
import com.codelink.clbackend.service.ProjectService;
import com.codelink.clbackend.service.TeamMessageService;
import com.codelink.clbackend.service.TeamService;
import com.codelink.clbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 团队消息接口
 */
@RestController
@RequestMapping("/teamMessages")
public class TeamMessagesController {

    @Resource UserService userService;
    @Resource ProjectService projectService;
    @Resource TeamService teamService;
    @Resource TeamMessageService teamMessageService;

    @PostMapping("/getTeamMessages")
    public BaseResponse<List<teamMessagesListDTO>> getTeamMessages(@RequestBody GetTeamMessagesRequest getTeamMessagesRequest, HttpServletRequest request) {
        if (getTeamMessagesRequest == null) {
            return new BaseResponse<>(500, null, "获取团队消息请求为空");
        }

        long uid = getTeamMessagesRequest.getUid();

        List<teamMessagesListDTO> result = teamMessageService.getTeamMessagesList(uid, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "获取团队消息失败");
        }

        return new BaseResponse<>(200, result, "获取团队消息成功");
    }

    @PostMapping("/applyJoinTeam")
    public BaseResponse<String> applyJoinTeam(@RequestBody ApplyJoinTeamRequest applyJoinTeamRequest, HttpServletRequest request) {
        if (applyJoinTeamRequest == null) {
            return new BaseResponse<>(500, null, "申请加入团队请求为空");
        }

        long uid = applyJoinTeamRequest.getUid();
        long pid = applyJoinTeamRequest.getPid();
        String remark = applyJoinTeamRequest.getRemark();

        String result = teamMessageService.applyJoinTeam(uid, pid, remark, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "申请加入团队失败");
        }
        if (result.equals("用户已加入项目")) {
            return new BaseResponse<>(402, null, "您已加入项目");
        }
        if (result.equals("用户未登录")) {
            return new BaseResponse<>(403, null, "用户未登录");
        }
        if (result.equals("用户已经申请过该项目")) {
            return new BaseResponse<>(404, null, "请勿重复申请");
        }

        return new BaseResponse<>(200, result, "申请加入团队成功");
    }

    @PostMapping("/agreeJoinApply")
    public BaseResponse<String> agreeJoinApply(@RequestBody AgreeJoinApplyRequest agreeJoinApplyRequest, HttpServletRequest request) {
        if (agreeJoinApplyRequest == null) {
            return new BaseResponse<>(500, null, "同意加入申请请求为空");
        }

        long uid = agreeJoinApplyRequest.getUid();
        long applyUserId = agreeJoinApplyRequest.getApplyUserId();
        long pid = agreeJoinApplyRequest.getPid();
        long tid = agreeJoinApplyRequest.getTid();
        long nid = agreeJoinApplyRequest.getNid();

        String result = teamMessageService.agreeJoinApply(uid, applyUserId, pid, tid, nid, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "同意加入申请失败");
        }
        if (result.equals("用户未登录")) {
            return new BaseResponse<>(402, null, "用户未登录");
        }
        if (result.equals("用户不是项目创建者")) {
            return new BaseResponse<>(403, null, "您不是项目创建者");
        }
        if (result.equals("用户已加入项目")) {
            return new BaseResponse<>(404, null, "您已加入项目");
        }
        if (result.equals("队伍已满员")) {
            return new BaseResponse<>(405, null, "队伍已满员");
        }

        return new BaseResponse<>(200, result, "同意加入申请成功");
    }

    @PostMapping("/rejectJoinApply")
    public BaseResponse<String> rejectJoinApply (@RequestBody RejectJoinApplyRequest rejectJoinApplyRequest, HttpServletRequest request) {
        if (rejectJoinApplyRequest == null) {
            return new BaseResponse<>(500, null, "拒绝加入申请请求为空");
        }

        long uid = rejectJoinApplyRequest.getUid();
        long applyUserId = rejectJoinApplyRequest.getApplyUserId();
        long pid = rejectJoinApplyRequest.getPid();
        long tid = rejectJoinApplyRequest.getTid();
        long nid = rejectJoinApplyRequest.getNid();

        String result = teamMessageService.rejectJoinApply(uid, applyUserId, pid, tid, nid, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "拒绝加入申请失败");
        }
        if (result.equals("用户未登录")) {
            return new BaseResponse<>(402, null, "用户未登录");
        }
        if (result.equals("用户不是项目创建者")) {
            return new BaseResponse<>(403, null, "您不是项目创建者");
        }

        return new BaseResponse<>(200, result, "拒绝加入申请成功");
    }

    @PostMapping("/getTeamMessagesFlag")
    public BaseResponse<Integer> getTeamMessagesFlag(@RequestBody GetTeamMessagesRequest getTeamMessagesRequest, HttpServletRequest request) {
        if (getTeamMessagesRequest == null) {
            return new BaseResponse<>(500, null, "获取团队消息标志请求为空");
        }

        long uid = getTeamMessagesRequest.getUid();

        int result = teamMessageService.getTeamMessagesFlag(uid, request);
        if (result == 0) {
            return new BaseResponse<>(200, result, "有未读消息");
        }
        if (result == 1) {
            return new BaseResponse<>(200, result, "无未读消息");
        }

        return new BaseResponse<>(401, null, "获取团队消息标志失败");
    }
}
