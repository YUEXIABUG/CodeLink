package com.codelink.clbackend.controller;

import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.model.domain.request.CheckJoinTeamPasswordRequest;
import com.codelink.clbackend.model.domain.request.QuitTeamRequest;
import com.codelink.clbackend.service.ProjectService;
import com.codelink.clbackend.service.TeamService;
import com.codelink.clbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 团队接口
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    @Resource
    ProjectService projectService;
    @Resource
    TeamService teamService;
    @Resource
    UserService userService;

    @PostMapping("/checkTeamPassword")
    public BaseResponse<Boolean> checkTeamPassword(@RequestBody CheckJoinTeamPasswordRequest checkJoinTeamPasswordRequest, HttpServletRequest request) {
        if (checkJoinTeamPasswordRequest == null) {
            return new BaseResponse<>(500, null, "检查团队密码请求为空");
        }

        long tid = checkJoinTeamPasswordRequest.getTid();
        String teamPassword = checkJoinTeamPasswordRequest.getTeamPassword();

        boolean result = teamService.checkTeamPassword(tid, teamPassword, request);
        if (!result) {
            return new BaseResponse<>(401, false, "团队密码错误");
        }

        return new BaseResponse<>(200, true, "团队密码正确");
    }

    @PostMapping("/quitTeam")
    public BaseResponse<Boolean> quitTeam(@RequestBody QuitTeamRequest quitTeamRequest, HttpServletRequest request) {
        if (quitTeamRequest == null) {
            return new BaseResponse<>(500, null, "退出团队请求为空");
        }

        long uid = quitTeamRequest.getUid();
        long pid = quitTeamRequest.getPid();
        long tid = quitTeamRequest.getTid();

        boolean result = teamService.quitTeam(uid, pid, tid, request);
        if (!result) {
            return new BaseResponse<>(401, false, "退出团队失败");
        }

        return new BaseResponse<>(200, true, "退出团队成功");
    }
}
