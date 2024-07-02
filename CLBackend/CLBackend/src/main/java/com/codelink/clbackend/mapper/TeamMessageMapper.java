package com.codelink.clbackend.mapper;

import com.codelink.clbackend.DTO.teamMessagesListDTO;
import com.codelink.clbackend.model.domain.TeamMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【team_message(队伍消息表)】的数据库操作Mapper
* @createDate 2024-04-03 10:23:40
* @Entity generator.domain.TeamMessage
*/
public interface TeamMessageMapper extends BaseMapper<TeamMessage> {

    List<teamMessagesListDTO> getTeamMessagesList(long uid);

    TeamMessage selectByApplyUserIdAndTid(long uid, long tid, int applyMessage);

    List<TeamMessage> selectByRecipientUid(long uid);

    List<TeamMessage> selectByRecipientUidAndIsRead(long uid, int isRead);
}




