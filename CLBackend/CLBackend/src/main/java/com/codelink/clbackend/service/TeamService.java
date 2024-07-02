package com.codelink.clbackend.service;

import com.codelink.clbackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author yuexiabug
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2024-03-26 10:41:33
*/
public interface TeamService extends IService<Team> {

    /**
     * 验证团队密码是否正确
     * @param tid 团队id
     * @param teamPassword 团队密码
     * @return 是否正确
     */
    boolean checkTeamPassword(long tid, String teamPassword, HttpServletRequest request);

    /**
     * 退出队伍
     * @param uid 用户id
     * @param pid 项目id
     * @param tid 队伍id
     * @return 是否成功
     */
    boolean quitTeam(long uid, long pid, long tid, HttpServletRequest request);
}
