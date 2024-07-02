package com.codelink.clbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.mapper.ProjectMapper;
import com.codelink.clbackend.mapper.TeamMessageMapper;
import com.codelink.clbackend.mapper.UserMapper;
import com.codelink.clbackend.model.domain.Project;
import com.codelink.clbackend.model.domain.Team;
import com.codelink.clbackend.model.domain.TeamMessage;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.TeamService;
import com.codelink.clbackend.mapper.TeamMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
* @author yuexiabug
* @description 针对表【team(队伍表)】的数据库操作Service实现
* @createDate 2024-03-26 10:41:33
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService{

    @Resource ProjectMapper projectMapper;
    @Resource TeamMapper teamMapper;
    @Resource UserMapper userMapper;
    @Resource TeamMessageMapper teamMessageMapper;

    /**
     * 密码加盐
     */
    private static final String SALT = "letUsFinishAProjectTogether";

    /**
     *用户登录太键
     */
    public static final String USER_LOGIN_STATE = "userLoginState";

    @Override
    public boolean checkTeamPassword(long tid, String teamPassword, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return false;
        }
        // 2、根据tid查询Team对象
        Team team = teamMapper.selectById(tid);
        // 3、检查teamPassword是否正确
        String NewteamPassword = DigestUtils.md5DigestAsHex((teamPassword + SALT).getBytes());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamPassword", NewteamPassword);
        Team team1 = teamMapper.selectOne(queryWrapper);
        if(team1 == null){
            return false;
        }
        return true;
    }

    @Override
    public boolean quitTeam(long uid, long pid, long tid, HttpServletRequest request) {
        // 1、检查用户登录
        User userLoginState = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userLoginState == null){
            return false;
        }
        // 2、根据uid查询User对象
        User user = userMapper.selectById(uid);
        // 3、将user表中的pid中删除pid，user表中的pid形如"[100001, 100002]"的字符串
        String pidStr = user.getPid();
        String[] pidListArray = pidStr.substring(1, pidStr.length() - 1).split(", ");
        String[] newPidListArray = new String[pidListArray.length - 1];
        for(int i = 0, j = 0; i < pidListArray.length; i++){
            if(Long.parseLong(pidListArray[i]) != pid){
                newPidListArray[j] = pidListArray[i];
                j++;
            }
        }
        String newPidStr = "[";
        for(int i = 0; i < newPidListArray.length; i++){
            newPidStr += newPidListArray[i];
            if(i != newPidListArray.length - 1){
                newPidStr += ", ";
            }
        }
        newPidStr += "]";
        user.setPid(newPidStr);
        int result1 = userMapper.updateById(user);
        if(result1 == 0){
            return false;
        }

        // 4、根据tid查询Team对象
        Team team = teamMapper.selectById(tid);
        // 5、将team表中的usersId中删除uid，team表中的usersId形如"[1000001, 1000002]"的字符串
        String usersIdStr = team.getUsersId();
        String[] usersIdListArray = usersIdStr.substring(1, usersIdStr.length() - 1).split(", ");
        String[] newUsersIdListArray = new String[usersIdListArray.length - 1];
        for(int i = 0, j = 0; i < usersIdListArray.length; i++){
            if(Long.parseLong(usersIdListArray[i]) != uid){
                newUsersIdListArray[j] = usersIdListArray[i];
                j++;
            }
        }
        String newUsersIdStr = "[";
        for(int i = 0; i < newUsersIdListArray.length; i++){
            newUsersIdStr += newUsersIdListArray[i];
            if(i != newUsersIdListArray.length - 1){
                newUsersIdStr += ", ";
            }
        }
        newUsersIdStr += "]";
        team.setUsersId(newUsersIdStr);
        int result2 = teamMapper.updateById(team);
        if(result2 == 0){
            return false;
        }

        // 6、给项目创建人发送消息
        long createrId = projectMapper.selectById(pid).getCreateUserId();
        TeamMessage teamMessage = new TeamMessage();
        teamMessage.setQuitMessage(1);
        teamMessage.setCreateUserId(createrId);
        teamMessage.setApplyUserId(uid);
        teamMessage.setRecipientUid(createrId);
        teamMessage.setIsRead(0);
        teamMessage.setTid(tid);
        teamMessageMapper.insert(teamMessage);

        return true;
    }
}




