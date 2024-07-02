package com.codelink.clbackend.service;

import com.codelink.clbackend.DTO.projectDetailDTO;
import com.codelink.clbackend.DTO.projectListDTO;
import com.codelink.clbackend.DTO.projectUsersDTO;
import com.codelink.clbackend.model.domain.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codelink.clbackend.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【project(项目表)】的数据库操作Service
* @createDate 2024-03-26 10:39:21
*/
public interface ProjectService extends IService<Project> {

    /**
     * 创建新项目
     * @param uid           用户id
     * @param projectName   项目名称
     * @param projectDes    项目描述
     * @param projectTags   项目标签
     * @param needTags      需求标签
     * @param teamAvatar    团队头像
     * @param isPersonal    是否个人项目
     * @param teamPassword  团队密码
     * @return 项目pid
     */
    long createNewProject(long uid, String projectName, String projectDes, String[] projectTags, String[] needTags, String teamAvatar, int isPersonal, String teamPassword, HttpServletRequest request);

    /**
     * 获取我的项目列表
     * @param uid 用户id
     * @return 项目列表
     */
    List<projectListDTO> getMyProjectList(long uid, HttpServletRequest request);

    /**
     * 获取已完成的项目列表
     * @param uid 用户id
     * @return 项目列表
     */
    List<projectListDTO> getFinishedProjectList(long uid, HttpServletRequest request);

    /**
     * 获取项目详情
     * @param pid 项目id
     * @return 项目详情
     */
    projectDetailDTO getProjectDetail(long pid, HttpServletRequest request);

    /**
     * 获取项目成员的信息
     * @param pid 项目id
     * @param tid 团队id
     * @return 项目成员信息
     */
    List<projectUsersDTO> getProjectUsers(long pid, long tid, HttpServletRequest request);

    /**
     * 修改项目信息
     * @param uid           用户id
     * @param pid           项目id
     * @param tid           团队id
     * @param projectName   项目名称
     * @param projectDes    项目描述
     * @param projectTags   项目标签
     * @param needTags      需求标签
     * @param teamAvatar    团队头像
     * @param isPersonal    是否个人项目
     * @param teamPassword  团队密码
     * @return 是否成功
     */
    boolean updateProject(long uid, long pid, long tid, String projectName, String projectDes, String[] projectTags, String[] needTags, String teamAvatar, int isPersonal, String teamPassword, HttpServletRequest request);

    /**
     * 获取推荐项目
     * @return 推荐项目列表
     */
    List<projectDetailDTO> recommendProjects(User loginUser, HttpServletRequest request);

    /**
     * 获取所有项目
     * @return 所有项目列表
     */
    List<projectDetailDTO> getAllProjects(HttpServletRequest request);

    /**
     * 完成项目
     * @param uid 用户id
     * @param pid 项目id
     * @return 是否成功
     */
    String finishProject(long uid, long pid, HttpServletRequest request);

    /**
     * 解散项目
     * @param uid 用户id
     * @param pid 项目id
     * @param tid 团队id
     * @return 是否成功
     */
    String breakProject(long uid, long pid, long tid, HttpServletRequest request);

    /**
     * 搜索项目
     * @param searchContent 搜索内容
     * @return 项目列表
     */
    List<projectDetailDTO> searchProjects(String searchContent, HttpServletRequest request);
}
