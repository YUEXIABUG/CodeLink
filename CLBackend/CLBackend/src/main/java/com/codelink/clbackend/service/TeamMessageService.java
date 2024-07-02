package com.codelink.clbackend.service;

import com.codelink.clbackend.DTO.teamMessagesListDTO;
import com.codelink.clbackend.model.domain.TeamMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【team_message(队伍消息表)】的数据库操作Service
* @createDate 2024-04-03 10:23:40
*/
public interface TeamMessageService extends IService<TeamMessage> {

    /**
     * 获取队伍消息列表
     * @param uid 用户id
     * @return 队伍消息列表
     */
    List<teamMessagesListDTO> getTeamMessagesList(long uid, HttpServletRequest request);

    /**
     * 申请加入队伍
     * @param uid 用户id
     * @param pid 项目id
     * @return 是否申请成功
     */
    String applyJoinTeam(long uid, long pid, String remark, HttpServletRequest request);

    /**
     * 同意加入申请
     * @param uid 用户id
     * @param applyUserId 申请用户id
     * @param pid 项目id
     * @param tid 队伍id
     * @param nid 通知id
     * @return 是否同意成功
     */
    String agreeJoinApply(long uid, long applyUserId, long pid, long tid, long nid, HttpServletRequest request);

    /**
     * 拒绝加入申请
     * @param uid 用户id
     * @param applyUserId 申请用户id
     * @param pid 项目id
     * @param tid 队伍id
     * @param nid 通知id
     * @return 是否拒绝成功
     */
    String rejectJoinApply(long uid, long applyUserId, long pid, long tid, long nid, HttpServletRequest request);

    /**
     * 获取消息未读标志
     * @param uid 用户id
     * @return 消息未读标志
     */
    int getTeamMessagesFlag(long uid, HttpServletRequest request);

}
