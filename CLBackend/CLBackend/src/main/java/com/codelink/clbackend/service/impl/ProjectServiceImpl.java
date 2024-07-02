package com.codelink.clbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.DTO.projectDetailDTO;
import com.codelink.clbackend.DTO.projectListDTO;
import com.codelink.clbackend.DTO.projectUsersDTO;
import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.mapper.TeamMapper;
import com.codelink.clbackend.mapper.TeamMessageMapper;
import com.codelink.clbackend.mapper.UserMapper;
import com.codelink.clbackend.model.domain.Project;
import com.codelink.clbackend.model.domain.Team;
import com.codelink.clbackend.model.domain.TeamMessage;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.ProjectService;
import com.codelink.clbackend.mapper.ProjectMapper;
import com.codelink.clbackend.service.UserService;
import com.codelink.clbackend.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author yuexiabug
* @description 针对表【project(项目表)】的数据库操作Service实现
* @createDate 2024-03-26 10:39:21
*/
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService{

    @Resource ProjectMapper projectMapper;
    @Resource TeamMapper teamMapper;
    @Resource UserMapper userMapper;
    @Resource TeamMessageMapper teamMessageMapper;
    @Resource UserService userService;

    /**
     * 密码加盐
     */
    private static final String SALT = "letUsFinishAProjectTogether";

    /**
     *用户登录太键
     */
    public static final String USER_LOGIN_STATE = "userLoginState";

    @Override
    public long createNewProject(long uid, String projectName, String projectDes, String[] projectTags, String[] needTags, String teamAvatar, int isPersonal, String teamPassword, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return -1;
        }
        // 2、检查项目名称、项目简介、队伍头像是否都不为空
        if (projectName == null || projectDes == null || teamAvatar == null) {
            return -1;
        }
        // 3、判断是否为个人项目，若是则密码必须在6-16位之间
        if (isPersonal == 1 && (teamPassword == null || teamPassword.length() < 6 || teamPassword.length() > 16)) {
            return -1;
        }
        // 4、创建项目
        Project project = new Project();
        project.setCreateUserId(uid);
        project.setProjectName(projectName);
        project.setProjectDes(projectDes);
        projectTags = Arrays.stream(projectTags).distinct().toArray(String[]::new);
        needTags = Arrays.stream(needTags).distinct().toArray(String[]::new);
        project.setProjectTags(Arrays.toString(projectTags));
        project.setNeedTags(Arrays.toString(needTags));
        project.setCreateTime(new Date());
        // 5、获得此项目pid
        projectMapper.insert(project);
        long pid = project.getPid();
        // 6、创建团队
        Team team = new Team();
        team.setPid(pid);
        team.setCreateUserId(uid);
        team.setTeamAvatar(teamAvatar);
        team.setIsPersonal(isPersonal);
        String password = DigestUtils.md5DigestAsHex((teamPassword + SALT).getBytes());
        team.setTeamPassword(password);
        team.setCreateTime(new Date());
        team.setUpdateTime(new Date());
        // 7、获得此团队tid
        teamMapper.insert(team);
        long tid = team.getTid();
        // 8、更新项目表中的团队id
        project.setTid(tid);
        projectMapper.updateById(project);
        // 9、更新用户表中的项目id
        User user = userMapper.selectById(uid);
        String projectIds = user.getPid();
        if (projectIds == null) {
            projectIds = "[" + pid + "]";
        } else {
            projectIds = projectIds.substring(0, projectIds.length() - 1) + ", " + pid + "]";
        }
        user.setPid(projectIds);
        userMapper.updateById(user);
        // 返回项目pid
        return pid;
    }

    @Override
    public List<projectListDTO> getMyProjectList(long uid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、新建一个projectListDTO的List
        List<projectListDTO> projectListDTOList = new ArrayList<>();
        // 3、通过user列表获取pid
        User user = userMapper.selectById(uid);
        String projectIds = user.getPid();
        // 4、遍历pid，通过pid获取project和team, 只遍历未完成以及未解散的项目
        if (projectIds != null) {
            String[] pidArray = projectIds.substring(1, projectIds.length() - 1).split(", ");
            for (String pid : pidArray) {
                Project project = projectMapper.selectById(pid);
                // 判断项目是否已完成
                if (project.getIsFinish() == 1){
                    continue;
                }
                Team team = teamMapper.selectById(project.getTid());
                // 判断团队是否已解散
                if (team.getIsDelete() == 1){
                    continue;
                }
                projectListDTO projectListDTO = new projectListDTO();
                projectListDTO.setProject(project);
                projectListDTO.setTeam(team);
                projectListDTO.setPid(project.getPid());
                projectListDTO.setProjectName(project.getProjectName());
                projectListDTO.setCreateUserId(project.getCreateUserId());
                projectListDTO.setProjectTags(project.getProjectTags());
                projectListDTO.setNeedTags(project.getNeedTags());
                projectListDTO.setTid(team.getTid());
                projectListDTO.setTeamAvatar(team.getTeamAvatar());
                projectListDTOList.add(projectListDTO);
            }
        }

        if (projectListDTOList.size() == 0) {
            return null;
        }
        return projectListDTOList;
    }

    @Override
    public List<projectListDTO> getFinishedProjectList(long uid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、新建一个projectListDTO的List
        List<projectListDTO> projectListDTOList = new ArrayList<>();
        // 3、通过user列表获取pid
        User user = userMapper.selectById(uid);
        String projectIds = user.getPid();
        // 4、遍历pid，通过pid获取project和team
        if (projectIds != null) {
            String[] pidArray = projectIds.substring(1, projectIds.length() - 1).split(", ");
            for (String pid : pidArray) {
                Project project = projectMapper.selectById(pid);
                // 判断项目是否已完成
                if (project.getIsFinish() == 0) {
                    continue;
                }
                Team team = teamMapper.selectById(project.getTid());
                projectListDTO projectListDTO = new projectListDTO();
                projectListDTO.setProject(project);
                projectListDTO.setTeam(team);
                projectListDTO.setPid(project.getPid());
                projectListDTO.setProjectName(project.getProjectName());
                projectListDTO.setCreateUserId(project.getCreateUserId());
                projectListDTO.setProjectTags(project.getProjectTags());
                projectListDTO.setNeedTags(project.getNeedTags());
                projectListDTO.setTid(team.getTid());
                projectListDTO.setTeamAvatar(team.getTeamAvatar());
                projectListDTOList.add(projectListDTO);
            }
        }

        if (projectListDTOList.size() == 0) {
            return null;
        }
        return projectListDTOList;
    }

    @Override
    public projectDetailDTO getProjectDetail(long pid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、通过pid获取project和team
        Project project = projectMapper.selectById(pid);
        Team team = teamMapper.selectById(project.getTid());
        // 3、新建一个projectDetailDTO
        projectDetailDTO projectDetailDTO = new projectDetailDTO();
        projectDetailDTO.setProject(project);
        projectDetailDTO.setTeam(team);
        return projectDetailDTO;
    }

    @Override
    public List<projectUsersDTO> getProjectUsers(long pid, long tid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、通过pid和tid获取project和team
        Project project = projectMapper.selectById(pid);
        Team team = teamMapper.selectById(tid);
        // 3、新建一个projectUsersDTO的List
        List<projectUsersDTO> projectUsersDTOList = new ArrayList<>();
        // 4、获取project中的createUserId
        long createUserId = project.getCreateUserId();
        User createUser = userMapper.selectById(createUserId);
        // 5、将createUser加入projectUsersDTOList，并为他设置userRole为1
        projectUsersDTO createUserDTO = new projectUsersDTO();
        createUserDTO.setUid(createUser.getUid());
        createUserDTO.setUsername(createUser.getUsername());
        createUserDTO.setUserAvatar(createUser.getUserAvatar());
        createUserDTO.setUserTags(createUser.getUserTags());
        createUserDTO.setUserRole(1);
        projectUsersDTOList.add(createUserDTO);
        // 6、获取team中的usersId, 其形如"[1000001, 1000007]"
        String usersId = team.getUsersId();
        if (usersId != null && !usersId.equals("[]")) {
            String[] userIdArray = usersId.substring(1, usersId.length() - 1).split(", ");
            for (String userId : userIdArray) {
                User user = userMapper.selectById(Long.parseLong(userId));
                projectUsersDTO projectUsersDTO = new projectUsersDTO();
                projectUsersDTO.setUid(user.getUid());
                projectUsersDTO.setUsername(user.getUsername());
                projectUsersDTO.setUserAvatar(user.getUserAvatar());
                projectUsersDTO.setUserTags(user.getUserTags());
                projectUsersDTO.setUserRole(0);
                projectUsersDTOList.add(projectUsersDTO);
            }
        }
        return projectUsersDTOList;
    }

    @Override
    public boolean updateProject(long uid, long pid, long tid, String projectName, String projectDes, String[] projectTags, String[] needTags, String teamAvatar, int isPersonal, String teamPassword, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return false;
        }
        // 2、检查pid的createrUserId是否为uid
        Project project = projectMapper.selectById(pid);
        if (project.getCreateUserId() != uid) {
            return false;
        }
        // 3、检查tid的createrUserId是否为uid
        Team team = teamMapper.selectById(tid);
        if (team.getCreateUserId() != uid) {
            return false;
        }
        // 4、检查项目名称、项目简介、队伍头像是否都不为空
        if (projectName == null || projectDes == null || teamAvatar == null) {
            return false;
        }
        // 5、判断是否为个人项目，若是则密码必须在6-16位之间
        if (isPersonal == 1 && (teamPassword == null || teamPassword.length() < 6 || teamPassword.length() > 16)) {
            return false;
        }
        // 6、对projectTags和needTags进行去重
        projectTags = Arrays.stream(projectTags).distinct().toArray(String[]::new);
        needTags = Arrays.stream(needTags).distinct().toArray(String[]::new);
        // 7、更新项目
        project.setProjectName(projectName);
        project.setProjectDes(projectDes);
        project.setProjectTags(Arrays.toString(projectTags));
        project.setNeedTags(Arrays.toString(needTags));
        projectMapper.updateById(project);
        // 8、更新团队
        team.setTeamAvatar(teamAvatar);
        team.setIsPersonal(isPersonal);
        String password = DigestUtils.md5DigestAsHex((teamPassword + SALT).getBytes());
        team.setTeamPassword(password);
        team.setUpdateTime(new Date());
        teamMapper.updateById(team);
        return true;
    }

    @Override
    public List<projectDetailDTO> recommendProjects(User loginUser, HttpServletRequest request) {
        int num = 20;
        List<projectDetailDTO> finalProjectList = new ArrayList<>();
        // 获取当前用户的标签
        String userTags = loginUser.getUserTags();
        // 获取所有项目需求标签
        QueryWrapper<Project> projectQueryWrapper = new QueryWrapper<>();
        projectQueryWrapper.select("pid", "tid", "projectName", "projectDes", "projectTags","needTags", "isFinish", "createUserId");
        projectQueryWrapper.isNotNull("needTags");
        projectQueryWrapper.ne("needTags", "[]");
        List<Project> projectList = this.list(projectQueryWrapper);

        Gson gson = new Gson();

        // 获取当前用户的pid，并将之转化为数组
        String userPid = loginUser.getPid();
        List<String> userPidList = gson.fromJson(userPid, new TypeToken<List<String>>() {
        }.getType());
        if (CollectionUtils.isEmpty(userPidList)) {
            userPidList = new ArrayList<>();
        }

        List<String> tagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
        }.getType());
        if(CollectionUtils.isEmpty(tagList)){
            return new ArrayList<>();
        } else {
            // 将每个标签转化为小写
            tagList = tagList.stream().map(String::toLowerCase).collect(Collectors.toList());
        }

        // 用户列表的下标 => 相似度
        List<Pair<Project, Long>> list = new ArrayList<>();
        // 依次计算每个项目需求标签与用户标签的相似度
        for (int i = 0; i < projectList.size(); i++) {
            Project project = projectList.get(i);
            String needTags = project.getNeedTags();
            // 获取项目对应的team
            Team team = teamMapper.selectById(project.getTid());
            // 无标签的项目、用户已经加入、或者项目所对应的team的isFull为1、已完成的、作为创建者、已解散的项目不推荐
            if (StringUtils.isBlank(needTags) || userPidList.contains(String.valueOf(project.getPid())) ||team.getIsFull() == 1 || project.getIsFinish() == 1 || team.getIsDelete() == 1 || project.getCreateUserId() == loginUser.getUid()) {
                continue;
            }
            List<String> needTagList = gson.fromJson(needTags, new TypeToken<List<String>>() {
            }.getType());
            // 将每个标签转化为小写
            needTagList = needTagList.stream().map(String::toLowerCase).collect(Collectors.toList());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, needTagList);
            list.add(new Pair<>(project, distance));
        }
        // 按编辑距离由大到小排序
        List<Pair<Project, Long>> topProjectPairList = list.stream()
                .sorted((a, b) -> (int) (b.getSecond() - a.getSecond()))
                .limit(num)
                .collect(Collectors.toList());
        List<projectDetailDTO> topProjectList = new ArrayList<>();
        for (Pair<Project, Long> pair : topProjectPairList) {
            Project project = pair.getFirst();
            Team team = teamMapper.selectById(project.getTid());
            projectDetailDTO projectDetailDTO = new projectDetailDTO();
            projectDetailDTO.setProject(project);
            projectDetailDTO.setTeam(team);
            topProjectList.add(projectDetailDTO);
        }
        //将topProjectList中的随机6个不相同的项目加入finalProjectList，如果不足6个则全部加入
        int size = topProjectList.size();
        if (size > 6) {
            List<Integer> indexList = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                int index = (int) (Math.random() * size);
                while (indexList.contains(index)) {
                    index = (int) (Math.random() * size);
                }
                indexList.add(index);
                finalProjectList.add(topProjectList.get(index));
            }
        } else {
            finalProjectList.addAll(topProjectList);
        }

        /******************************以上是编辑距离算法，以下是协同过滤算法********************************/

        // 调用UserServiceImpl中的recommendUsers方法匹配与该用户标签最相似的10个用户
        List<User> recommendUsers = userService.recommendUsers(loginUser, request);
        if (recommendUsers != null) {
            recommendUsers = recommendUsers.stream().limit(10).collect(Collectors.toList());
        }
        // 随机选取其中的5个不相同用户
        List<String> recommendUserPidList = new ArrayList<>();
        if (recommendUsers != null) {
            int recommendUsersSize = recommendUsers.size();
            if (recommendUsersSize > 5) {
                List<Integer> indexList = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    int index = (int) (Math.random() * recommendUsersSize);
                    while (indexList.contains(index)) {
                        index = (int) (Math.random() * recommendUsersSize);
                    }
                    indexList.add(index);
                    String pid = recommendUsers.get(index).getPid();
                    // 若用户没有项目，则跳过
                    if (StringUtils.isBlank(pid)) {
                        continue;
                    }
                    List<String> pidList = gson.fromJson(pid, new TypeToken<List<String>>() {
                    }.getType());
                    recommendUserPidList.addAll(pidList);
                }
            } else {
                for (User user : recommendUsers) {
                    String pid = user.getPid();
                    // 若用户没有项目，则跳过
                    if (StringUtils.isBlank(pid)) {
                        continue;
                    }
                    List<String> pidList = gson.fromJson(pid, new TypeToken<List<String>>() {
                    }.getType());
                    recommendUserPidList.addAll(pidList);
                }
            }
        }

        // 查询每个pid的项目的需求标签，计算与用户标签的相似度
        List<Pair<Project, Long>> recommendProjectPairList = new ArrayList<>();
        for (String pid : recommendUserPidList) {
            Project project = projectMapper.selectById(pid);
            Team team = teamMapper.selectById(project.getTid());
            String needTags = project.getNeedTags();
            if (StringUtils.isBlank(needTags) || userPidList.contains(String.valueOf(project.getPid())) || team.getIsFull() == 1 || project.getIsFinish() == 1 || team.getIsDelete() == 1 || project.getCreateUserId() == loginUser.getUid()) {
                continue;
            }
            List<String> needTagList = gson.fromJson(needTags, new TypeToken<List<String>>() {
            }.getType());
            // 将每个标签转化为小写
            needTagList = needTagList.stream().map(String::toLowerCase).collect(Collectors.toList());
            long distance = AlgorithmUtils.minDistance(tagList, needTagList);
            recommendProjectPairList.add(new Pair<>(project, distance));
        }
        // 按编辑距离由大到小排序
        List<Pair<Project, Long>> topRecommendProjectPairList = recommendProjectPairList.stream()
                .sorted((a, b) -> (int) (b.getSecond() - a.getSecond()))
                .limit(num)
                .collect(Collectors.toList());
        List<projectDetailDTO> topRecommendProjectList = new ArrayList<>();
        for (Pair<Project, Long> pair : topRecommendProjectPairList) {
            Project project = pair.getFirst();
            Team team = teamMapper.selectById(project.getTid());
            projectDetailDTO projectDetailDTO = new projectDetailDTO();
            projectDetailDTO.setProject(project);
            projectDetailDTO.setTeam(team);
            topRecommendProjectList.add(projectDetailDTO);
        }
        //将topRecommendProjectList中的随机4个不相同项目加入finalProjectList，如果不足4个则全部加入
        int recommendSize = topRecommendProjectList.size();
        if (recommendSize > 4) {
            List<Integer> indexList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                int index = (int) (Math.random() * recommendSize);
                while (indexList.contains(index)) {
                    index = (int) (Math.random() * recommendSize);
                }
                indexList.add(index);
                finalProjectList.add(topRecommendProjectList.get(index));
            }
        } else {
            finalProjectList.addAll(topRecommendProjectList);
        }

        return finalProjectList;
    }

    @Override
    public List<projectDetailDTO> getAllProjects(HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、新建一个projectDetailDTO的List
        List<projectDetailDTO> projectDetailDTOList = new ArrayList<>();
        // 3、获取所有项目
        List<Project> projectList = projectMapper.selectList(null);
        // 4、遍历项目，获取project和team
        for (Project project : projectList) {
            // 如果项目已完成或者团队已解散，则去除
            Team team = teamMapper.selectById(project.getTid());
            if (project.getIsFinish() == 1 || team.getIsFull() == 1 || team.getIsDelete() == 1) {
                continue;
            }
            projectDetailDTO projectDetailDTO = new projectDetailDTO();
            projectDetailDTO.setProject(project);
            projectDetailDTO.setTeam(team);
            projectDetailDTOList.add(projectDetailDTO);
        }
        // 根据创建时间降序排序
        projectDetailDTOList.sort((a, b) -> b.getProject().getCreateTime().compareTo(a.getProject().getCreateTime()));
        return projectDetailDTOList;
    }

    @Override
    public String finishProject(long uid, long pid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return "用户未登录";
        }
        // 2、检查pid的createrUserId是否为uid
        Project project = projectMapper.selectById(pid);
        if (project.getCreateUserId() != uid) {
            return "用户无权限";
        }
        // 3、查询项目对应的队伍tid
        long tid = project.getTid();
        // 4、向团队创建者发送消息
        Team team = teamMapper.selectById(tid);
        long createUserId = team.getCreateUserId();
        TeamMessage teamMessage1 = new TeamMessage();
        teamMessage1.setApplyUserId(uid);
        teamMessage1.setRecipientUid(createUserId);
        teamMessage1.setCreateUserId(createUserId);
        teamMessage1.setTid(tid);
        teamMessage1.setFinishedMessage(1);
        teamMessage1.setIsRead(0);
        teamMessageMapper.insert(teamMessage1);
        // 5、向团队成员发送消息
        String usersId = team.getUsersId();
        if (usersId != null) {
            String[] userIdArray = usersId.substring(1, usersId.length() - 1).split(", ");
            for (String userId : userIdArray) {
                TeamMessage teamMessage2 = new TeamMessage();
                teamMessage2.setApplyUserId(uid);
                teamMessage2.setRecipientUid(Long.parseLong(userId));
                teamMessage2.setCreateUserId(createUserId);
                teamMessage2.setTid(tid);
                teamMessage2.setFinishedMessage(1);
                teamMessage2.setIsRead(0);
                teamMessageMapper.insert(teamMessage2);
            }
        }
        // 6、更新项目
        project.setIsFinish(1);
        projectMapper.updateById(project);

        return "项目已完成";
    }

    @Override
    public String breakProject(long uid, long pid, long tid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return "用户未登录";
        }
        // 2、检查pid的createrUserId是否为uid
        Project project = projectMapper.selectById(pid);
        if (project.getCreateUserId() != uid) {
            return "用户无权限";
        }
        // 3、检查tid的createrUserId是否为uid
        Team team = teamMapper.selectById(tid);
        if (team.getCreateUserId() != uid) {
            return "用户无权限";
        }
        // 4、向团队创建者发送消息
        long createUserId = team.getCreateUserId();
        TeamMessage teamMessage1 = new TeamMessage();
        teamMessage1.setApplyUserId(uid);
        teamMessage1.setRecipientUid(createUserId);
        teamMessage1.setCreateUserId(createUserId);
        teamMessage1.setTid(tid);
        teamMessage1.setBreakMessage(1);
        teamMessage1.setIsRead(0);
        teamMessageMapper.insert(teamMessage1);
        // 5、向团队成员发送消息
        String usersId = team.getUsersId();
        if (usersId != null) {
            String[] userIdArray = usersId.substring(1, usersId.length() - 1).split(", ");
            for (String userId : userIdArray) {
                TeamMessage teamMessage2 = new TeamMessage();
                teamMessage2.setApplyUserId(uid);
                teamMessage2.setRecipientUid(Long.parseLong(userId));
                teamMessage2.setCreateUserId(createUserId);
                teamMessage2.setTid(tid);
                teamMessage2.setBreakMessage(1);
                teamMessage2.setIsRead(0);
                teamMessageMapper.insert(teamMessage2);
            }
        }
        // 6、更新团队，设置团队的deleteTime为当前时间，设置团队而isDelete为1
        team.setDeleteTime(new Date());
        team.setIsDelete(1);
        teamMapper.updateById(team);

        return "项目已解散";
    }

    @Override
    public List<projectDetailDTO> searchProjects(String searchContent, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、新建一个projectDetailDTO的List
        List<projectDetailDTO> projectDetailDTOList = new ArrayList<>();
        // 3、获取所有项目
        List<Project> projectList = projectMapper.selectList(null);
        // 4、遍历项目，获取project和team
        for (Project project : projectList) {
            // 如果项目已完成或者团队已解散，则去除
            Team team = teamMapper.selectById(project.getTid());
            if (project.getIsFinish() == 1 || team.getIsFull() == 1 || team.getIsDelete() == 1) {
                continue;
            }
            // 如果项目名称包含搜索内容，则加入projectDetailDTOList
            if (project.getProjectName().contains(searchContent)) {
                projectDetailDTO projectDetailDTO = new projectDetailDTO();
                projectDetailDTO.setProject(project);
                projectDetailDTO.setTeam(team);
                projectDetailDTOList.add(projectDetailDTO);
            }
        }
        // 根据创建时间降序排序
        projectDetailDTOList.sort((a, b) -> b.getProject().getCreateTime().compareTo(a.getProject().getCreateTime()));
        return projectDetailDTOList;
    }
}




