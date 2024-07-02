package com.codelink.clbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.DTO.teamMessagesListDTO;
import com.codelink.clbackend.mapper.ProjectMapper;
import com.codelink.clbackend.mapper.TeamMapper;
import com.codelink.clbackend.mapper.UserMapper;
import com.codelink.clbackend.model.domain.Project;
import com.codelink.clbackend.model.domain.Team;
import com.codelink.clbackend.model.domain.TeamMessage;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.TeamMessageService;
import com.codelink.clbackend.mapper.TeamMessageMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author yuexiabug
* @description 针对表【team_message(队伍消息表)】的数据库操作Service实现
* @createDate 2024-04-03 10:23:40
*/
@Service
public class TeamMessageServiceImpl extends ServiceImpl<TeamMessageMapper, TeamMessage> implements TeamMessageService{

    @Resource UserMapper userMapper;
    @Resource ProjectMapper projectMapper;
    @Resource TeamMessageMapper teamMessageMapper;
    @Resource TeamMapper teamMapper;

    /**
     *用户登录太键
     */
    public static final String USER_LOGIN_STATE = "userLoginState";

    @Override
    public List<teamMessagesListDTO> getTeamMessagesList(long uid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return null;
        }
        // 2、根据uid查询所有recipientUid为uid的TeamMessage对象
        List<TeamMessage> teamMessages = teamMessageMapper.selectByRecipientUid(uid);
        // 3、将队伍消息列表中的isRead字段设置为1，并更新数据库
        for (TeamMessage teamMessage : teamMessages) {
            // 3.1、将数据库中的isRead字段设置为1
            teamMessage.setIsRead(1);
            teamMessageMapper.updateById(teamMessage);
        }
        // 4、查找队伍消息列表中的用户信息、项目信息、队伍信息
        List<teamMessagesListDTO> teamMessagesList = new ArrayList<>();
        for (TeamMessage teamMessage : teamMessages) {
            teamMessagesListDTO teamMessagesListDTO = new teamMessagesListDTO();
            teamMessagesListDTO.setTeamMessage(teamMessage);
            teamMessagesList.add(teamMessagesListDTO);
        }
        // 5、将这些信息封装在teamMessagesListDTO中
        for (teamMessagesListDTO teamMessagesListDTO : teamMessagesList) {
            User user = userMapper.selectById(teamMessagesListDTO.getTeamMessage().getApplyUserId());
            teamMessagesListDTO.setUser(user);
            Team team = teamMapper.selectById(teamMessagesListDTO.getTeamMessage().getTid());
            teamMessagesListDTO.setTeam(team);
            Project project = projectMapper.selectById(team.getPid());
            teamMessagesListDTO.setProject(project);
        }
        // 6、将teamMessagesList倒序排列
        List<teamMessagesListDTO> teamMessagesList1 = new ArrayList<>();
        for (int i = teamMessagesList.size() - 1; i >= 0; i--) {
            teamMessagesList1.add(teamMessagesList.get(i));
        }

        // 6、返回teamMessagesList
        return teamMessagesList1;
    }

    @Override
    public String applyJoinTeam(long uid, long pid, String remark, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return "用户未登录";
        }
        // 2、查询User表中的pid，判断用户是否已经加入项目
        User user = userMapper.selectById(uid);
        String pidList = user.getPid();
        if (pidList == null) {
            pidList = "";
        }
        if(pidList.contains(String.valueOf(pid))){
            return "用户已加入项目";
        }
        // 3、查找项目对应的队伍id和createUserId
        Project project = projectMapper.selectById(pid);
        long tid = project.getTid();
        long createUserId = project.getCreateUserId();
        // 4、重复申请
        TeamMessage teamMessage1 = teamMessageMapper.selectByApplyUserIdAndTid(uid, tid, 0);
        if (teamMessage1 != null) {
            return "用户已经申请过该项目";
        }
        // 5、创建TeamMessage对象
        TeamMessage teamMessage = new TeamMessage();
        teamMessage.setApplyUserId(uid);
        teamMessage.setTid(tid);
        teamMessage.setCreateUserId(createUserId);
        teamMessage.setRecipientUid(createUserId);
        teamMessage.setRemark(remark);
        teamMessage.setApplyMessage(0);
        teamMessage.setIsRead(0);
        // 6、插入TeamMessage对象
        teamMessageMapper.insert(teamMessage);
        return "申请成功";
    }

    @Override
    public String agreeJoinApply(long uid, long applyUserId, long pid, long tid, long nid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return "用户未登录";
        }
        // 2、查询User表中的pid，判断用户是否已经加入项目
        User user = userMapper.selectById(applyUserId);
        String pidList = user.getPid();
        if (pidList == null) {
            pidList = "";
        }
        if(pidList.contains(String.valueOf(pid))){
            return "用户已加入项目";
        }
        // 3、查看队伍是否已经满员
        Team team = teamMapper.selectById(tid);
        int isFull = team.getIsFull();
        if (isFull == 1) {
            return "队伍已满员";
        }
        // 4、更新TeamMessage表中的applyMessage字段
        TeamMessage teamMessage = teamMessageMapper.selectById(nid);
        teamMessage.setApplyMessage(1);
        teamMessageMapper.updateById(teamMessage);
        // 5、更新User表中的pid字段, user表中的pid字段是一个形如"[1000001, 1000002]"的字符串
        String pidList1 = user.getPid();
        if (pidList1 == null) {
            pidList1 = "[]";
        }
        String[] pidListArray = pidList1.substring(1, pidList1.length() - 1).split(", ");
        String[] newPidListArray = new String[pidListArray.length + 1];
        for (int i = 0; i < pidListArray.length; i++) {
            newPidListArray[i] = pidListArray[i];
        }
        newPidListArray[pidListArray.length] = String.valueOf(pid);
        // newPidListArray有可能会是一个"["", 1000001]"的字符串，需要将""以及,删除
        if (newPidListArray[0].equals("")) {
            String[] newPidListArray1 = new String[newPidListArray.length - 1];
            for (int i = 0; i < newPidListArray1.length; i++) {
                newPidListArray1[i] = newPidListArray[i + 1];
            }
            newPidListArray = newPidListArray1;
        }
        StringBuilder newPidList = new StringBuilder("[");
        for (int i = 0; i < newPidListArray.length; i++) {
            newPidList.append(newPidListArray[i]);
            if (i != newPidListArray.length - 1) {
                newPidList.append(", ");
            }
        }
        newPidList.append("]");
        user.setPid(newPidList.toString());
        userMapper.updateById(user);
        // 6、获取team中的usersId字段
        String usersIds = team.getUsersId();
        // 7、usersIds是一个形如"[1000001, 1000002]"的字符串，将其转化为一个数组
        // 若usersIds为空，则usersIdsArray为空数组
        if (usersIds == null) {
            usersIds = "[]";
        }

        String[] usersIdsArray = usersIds.substring(1, usersIds.length() - 1).split(", ");
        // 更改isFull字段
        int currentNum = usersIdsArray.length;
        currentNum = currentNum + 2;
        if (currentNum == 5) {
            team.setIsFull(1);
        }
        // 8、将uid加入usersIdsArray
        String[] newUsersIdsArray = new String[usersIdsArray.length + 1];
        for (int i = 0; i < usersIdsArray.length; i++) {
            newUsersIdsArray[i] = usersIdsArray[i];
        }
        newUsersIdsArray[usersIdsArray.length] = String.valueOf(applyUserId);
        // newUsersIdsArray有可能会是一个"["", 1000001]"的字符串，需要将""以及,删除
        if (newUsersIdsArray[0].equals("")) {
            String[] newUsersIdsArray1 = new String[newUsersIdsArray.length - 1];
            for (int i = 0; i < newUsersIdsArray1.length; i++) {
                newUsersIdsArray1[i] = newUsersIdsArray[i + 1];
            }
            newUsersIdsArray = newUsersIdsArray1;
        }
        // 9、将newUsersIdsArray转化为一个形如"[1000001, 1000002]"的字符串
        StringBuilder newUsersIds = new StringBuilder("[");
        for (int i = 0; i < newUsersIdsArray.length; i++) {
            newUsersIds.append(newUsersIdsArray[i]);
            if (i != newUsersIdsArray.length - 1) {
                newUsersIds.append(", ");
            }
        }
        newUsersIds.append("]");
        // 10、更新team中的usersId字段
        team.setUsersId(newUsersIds.toString());
        teamMapper.updateById(team);

        return "同意成功";
    }

    @Override
    public String rejectJoinApply(long uid, long applyUserId, long pid, long tid, long nid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return "用户未登录";
        }
        // 2、判断用户是否是项目创建者
        Project project = projectMapper.selectById(pid);
        if (project.getCreateUserId() != uid) {
            return "用户不是项目创建者";
        }
        // 3、更新TeamMessage表中的applyMessage字段
        TeamMessage teamMessage = teamMessageMapper.selectById(nid);
        teamMessage.setApplyMessage(2);
        teamMessageMapper.updateById(teamMessage);
        return "拒绝成功";
    }

    @Override
    public int getTeamMessagesFlag(long uid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return 0;
        }
        // 2、根据uid查询所有recipientUid为uid且isRead为0的TeamMessage对象
        List<TeamMessage> teamMessages = teamMessageMapper.selectByRecipientUidAndIsRead(uid, 0);
        if(teamMessages.size() > 0){
            return 0;
        }
        return 1;
    }
}




