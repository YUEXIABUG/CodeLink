package com.codelink.clbackend.service;

import com.codelink.clbackend.DTO.ChatDTO;
import com.codelink.clbackend.model.domain.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【chat(聊天消息表)】的数据库操作Service
* @createDate 2024-04-13 10:43:41
*/
public interface ChatService extends IService<Chat> {
    /**
     * 获取好友聊天记录
     * @param uid       用户id
     * @param friendUid  好友id
     * @return ChatDTO
     */
    List<ChatDTO> getFriendsChatRecord(long uid, long friendUid, HttpServletRequest request);

    /**
     * 发送好友聊天消息
     * @param uid       用户id
     * @param friendId  好友id
     * @param text      消息内容
     * @return 是否成功
     */
    boolean sendFriendsChatMessage(long uid, long friendId, String text, HttpServletRequest request);

    /**
     * 撤回聊天消息
     * @param chatId    聊天消息id
     * @param uid       用户id
     * @return 是否成功
     */
    boolean withdrawChatMessage(long chatId, long uid, HttpServletRequest request);

    /**
     * 获取队伍聊天记录
     * @param uid       用户id
     * @param tid       队伍id
     * @return ChatDTO
     */
    List<ChatDTO> getTeamChatRecord(long uid, long tid, HttpServletRequest request);

    /**
     * 发送队伍聊天消息
     * @param uid       用户id
     * @param tid       队伍id
     * @param text      消息内容
     * @return 是否成功
     */
    boolean sendTeamChatMessage(long uid, long tid, String text, HttpServletRequest request);
}
