package com.codelink.clbackend.controller;

import com.codelink.clbackend.DTO.projectDetailDTO;
import com.codelink.clbackend.DTO.projectListDTO;
import com.codelink.clbackend.DTO.projectUsersDTO;
import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.model.domain.request.*;
import com.codelink.clbackend.service.ProjectService;
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
 * 项目接口
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Resource ProjectService projectService;
    @Resource TeamService teamService;
    @Resource UserService userService;

    @PostMapping("/createNewProject")
    public BaseResponse<Long> createNewProject(@RequestBody CreateNewProjectRequest createNewProjectRequest, HttpServletRequest request) {
        if (createNewProjectRequest == null) {
            return new BaseResponse<>(500, null, "创建项目请求为空");
        }

        long uid = createNewProjectRequest.getUid();
        String projectName = createNewProjectRequest.getProjectName();
        String projectDes = createNewProjectRequest.getProjectDes();
        String[] projectTags = createNewProjectRequest.getProjectTags();
        String[] needTags = createNewProjectRequest.getNeedTags();
        String teamAvatar = createNewProjectRequest.getTeamAvatar();
        int isPersonal = createNewProjectRequest.getIsPersonal();
        String teamPassword = createNewProjectRequest.getTeamPassword();

        long result = projectService.createNewProject(uid, projectName, projectDes, projectTags, needTags, teamAvatar, isPersonal, teamPassword, request);
        if (result == -1) {
            return new BaseResponse<>(401, null, "创建项目失败");
        }

        return new BaseResponse<>(200, result, "创建项目成功");
    }

    @PostMapping("/getMyProjectList")
    public BaseResponse<List<projectListDTO>> getMyProjectList(@RequestBody GetMyProjectListRequest getMyProjectListRequest, HttpServletRequest request) {
        if (getMyProjectListRequest == null) {
            return new BaseResponse<>(500, null, "获取项目列表请求为空");
        }

        long uid = getMyProjectListRequest.getUid();

        List<projectListDTO> result = projectService.getMyProjectList(uid, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "获取项目列表失败");
        }

        return new BaseResponse<>(200, result, "获取项目列表成功");
    }

    @PostMapping("/getFinishedProjectList")
    public BaseResponse<List<projectListDTO>> getFinishedProjectList(@RequestBody GetFinishedProjectListRequest getFinishedProjectListRequest, HttpServletRequest request) {
        if (getFinishedProjectListRequest == null) {
            return new BaseResponse<>(500, null, "获取项目列表请求为空");
        }

        long uid = getFinishedProjectListRequest.getUid();

        List<projectListDTO> result = projectService.getFinishedProjectList(uid, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "获取项目列表失败");
        }

        return new BaseResponse<>(200, result, "获取项目列表成功");
    }

    @PostMapping("/getProjectDetail")
    public BaseResponse<projectDetailDTO> getProjectDetail(@RequestBody GetProjectDetailRequest getProjectDetailRequest, HttpServletRequest request) {
        if (getProjectDetailRequest == null) {
            return new BaseResponse<>(500, null, "获取项目详情请求为空");
        }

        long projectId = getProjectDetailRequest.getPid();

        projectDetailDTO result = projectService.getProjectDetail(projectId, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "获取项目详情失败");
        }

        return new BaseResponse<>(200, result, "获取项目详情成功");
    }

    @PostMapping("/getProjectUsers")
    public BaseResponse<List<projectUsersDTO>> getProjectUsers(@RequestBody GetProjectUsersInfoRequest getProjectUsersInfoRequest, HttpServletRequest request) {
        if (getProjectUsersInfoRequest == null) {
            return new BaseResponse<>(500, null, "获取项目成员请求为空");
        }

        long pid = getProjectUsersInfoRequest.getPid();
        long tid = getProjectUsersInfoRequest.getTid();

        List<projectUsersDTO> result = projectService.getProjectUsers(pid, tid, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "获取项目成员失败");
        }

        return new BaseResponse<>(200, result, "获取项目成员成功");
    }

    @PostMapping("/updateProject")
    public BaseResponse<Boolean> updateProject(@RequestBody EditProjectInfoRequest editProjectInfoRequest, HttpServletRequest request) {
        if (editProjectInfoRequest == null) {
            return new BaseResponse<Boolean>(500, null, "修改项目请求为空");
        }

        long uid = editProjectInfoRequest.getUid();
        long pid = editProjectInfoRequest.getPid();
        long tid = editProjectInfoRequest.getTid();
        String projectName = editProjectInfoRequest.getProjectName();
        String projectDes = editProjectInfoRequest.getProjectDes();
        String[] projectTags = editProjectInfoRequest.getProjectTags();
        String[] needTags = editProjectInfoRequest.getNeedTags();
        String teamAvatar = editProjectInfoRequest.getTeamAvatar();
        int isPersonal = editProjectInfoRequest.getIsPersonal();
        String teamPassword = editProjectInfoRequest.getTeamPassword();

        boolean result = projectService.updateProject(uid, pid, tid, projectName, projectDes, projectTags, needTags, teamAvatar, isPersonal, teamPassword, request);
        if (!result) {
            return new BaseResponse<Boolean>(401, null, "修改项目失败");
        }

        return new BaseResponse<Boolean>(200, result, "修改项目成功");
    }

    @PostMapping("/recommendProjects")
    public BaseResponse<List<projectDetailDTO>> recommendProjects(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            return new BaseResponse<>(401, null, "用户未登录");
        }
        List<projectDetailDTO> recommendProjects = projectService.recommendProjects(loginUser, request);
        if (recommendProjects == null) {
            return new BaseResponse<>(400, null, "获取推荐项目失败");
        }
        return new BaseResponse<>(200, recommendProjects, "获取推荐项目成功");
    }

    @PostMapping("/getAllProjects")
    public BaseResponse<List<projectDetailDTO>> getAllProjects(HttpServletRequest request) {
        List<projectDetailDTO> allProjects = projectService.getAllProjects(request);
        if (allProjects == null) {
            return new BaseResponse<>(400, null, "获取所有项目失败");
        }
        return new BaseResponse<>(200, allProjects, "获取所有项目成功");
    }

    @PostMapping("/finishProject")
    public BaseResponse<String> finishProject(@RequestBody FinishProjectRequest finishProjectRequest, HttpServletRequest request) {
        if (finishProjectRequest == null) {
            return new BaseResponse<>(500, null, "完成项目请求为空");
        }

        long uid = finishProjectRequest.getUid();
        long pid = finishProjectRequest.getPid();

        String result = projectService.finishProject(uid, pid, request);
        if (result == "用户未登录") {
            return new BaseResponse<>(401, null, "用户未登录");
        } else if (result == "用户无权限") {
            return new BaseResponse<>(401, null, "您无权限设置项目完成");
        }

        return new BaseResponse<>(200, result, "设置项目完成成功");
    }

    @PostMapping("/breakProject")
    public BaseResponse<String> breakProject(@RequestBody BreakProjectRequest breakProjectRequest, HttpServletRequest request) {
        if (breakProjectRequest == null) {
            return new BaseResponse<>(500, null, "解散项目请求为空");
        }

        long uid = breakProjectRequest.getUid();
        long pid = breakProjectRequest.getPid();
        long tid = breakProjectRequest.getTid();

        String result = projectService.breakProject(uid, pid, tid, request);
        if (result == "用户未登录") {
            return new BaseResponse<>(401, null, "用户未登录");
        } else if (result == "用户无权限") {
            return new BaseResponse<>(402, null, "您无权限解散项目");
        }

        return new BaseResponse<>(200, result, "解散项目成功");
    }

    @PostMapping("/searchProject")
    public BaseResponse<List<projectDetailDTO>> searchProject(@RequestBody SearchProjectRequest searchProjectRequest, HttpServletRequest request) {
        if (searchProjectRequest == null) {
            return new BaseResponse<>(500, null, "搜索项目请求为空");
        }

        String searchContent = searchProjectRequest.getSearchContent();

        List<projectDetailDTO> result = projectService.searchProjects(searchContent, request);
        if (result == null) {
            return new BaseResponse<>(401, null, "搜索项目失败");
        }

        return new BaseResponse<>(200, result, "搜索项目成功");
    }
}
