package com.codelink.clbackend.controller;

import com.codelink.clbackend.DTO.ChatDTO;
import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.model.domain.request.*;
import com.codelink.clbackend.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    ChatService chatService;

    /**
     * 获取好友聊天记录
     */
    @PostMapping("/getFriendsChatRecord")
    public BaseResponse<List<ChatDTO>> getFriendsChatRecord(@RequestBody GetFriendsChatRecordRequest getFriendsChatRecordRequest, HttpServletRequest request) {
        if (getFriendsChatRecordRequest == null) {
            return new BaseResponse<>(500, null, "获取聊天记录请求为空");
        }

        long uid = getFriendsChatRecordRequest.getUid();
        long friendUid = getFriendsChatRecordRequest.getFriendUid();
        if (uid <= 0 || friendUid <= 0) {
            return new BaseResponse<>(400, null, "用户id为空");
        }

        List<ChatDTO> chatDTOList = chatService.getFriendsChatRecord(uid, friendUid, request);
        if (chatDTOList == null) {
            return new BaseResponse<>(401, null, "获取聊天记录失败");
        }

        return new BaseResponse<>(200, chatDTOList, "获取聊天记录成功");
    }

    /**
     * 发送好友聊天消息
     */
    @PostMapping("/sendFriendsChatMessage")
    public BaseResponse<Boolean> sendFriendsChatMessage(@RequestBody SendFriendsChatMessageRequest sendFriendsChatMessageRequest, HttpServletRequest request) {
        if (sendFriendsChatMessageRequest == null) {
            return new BaseResponse<>(500, null, "发送聊天消息请求为空");
        }

        long uid = sendFriendsChatMessageRequest.getUid();
        long friendUid = sendFriendsChatMessageRequest.getFriendUid();
        String text = sendFriendsChatMessageRequest.getText();
        if (uid <= 0 || friendUid <= 0 || text == null || text.isEmpty()) {
            return new BaseResponse<>(400, false, "用户id或好友id或消息内容为空");
        }

        boolean result = chatService.sendFriendsChatMessage(uid, friendUid, text, request);
        if (!result) {
            return new BaseResponse<>(401, false, "发送聊天消息失败");
        }

        return new BaseResponse<>(200, true, "发送聊天消息成功");
    }

    /**
     * 撤回消息
     */
    @PostMapping("/withdrawChatMessage")
    public BaseResponse<Boolean> withdrawChatMessage(@RequestBody WithdrawChatMessageRequest withdrawChatMessageRequest, HttpServletRequest request) {
        if (withdrawChatMessageRequest == null) {
            return new BaseResponse<>(500, null, "撤回消息请求为空");
        }

        long chatId = withdrawChatMessageRequest.getChatId();
        long uid = withdrawChatMessageRequest.getUid();
        if (chatId <= 0 || uid <= 0) {
            return new BaseResponse<>(400, false, "聊天消息id或用户id为空");
        }

        boolean result = chatService.withdrawChatMessage(chatId, uid, request);
        if (!result) {
            return new BaseResponse<>(401, false, "撤回消息失败");
        }

        return new BaseResponse<>(200, true, "撤回消息成功");
    }

    /**
     * 获取队伍聊天记录
     */
    @PostMapping("/getTeamChatRecord")
    public BaseResponse<List<ChatDTO>> getTeamChatRecord(@RequestBody GetTeamChatRecordRequest getTeamChatRecordRequest, HttpServletRequest request) {
        if (getTeamChatRecordRequest == null) {
            return new BaseResponse<>(500, null, "获取聊天记录请求为空");
        }

        long uid = getTeamChatRecordRequest.getUid();
        long tid = getTeamChatRecordRequest.getTid();
        if (uid <= 0 || tid <= 0) {
            return new BaseResponse<>(400, null, "用户id或队伍id为空");
        }

        List<ChatDTO> chatDTOList = chatService.getTeamChatRecord(uid, tid, request);
        if (chatDTOList == null) {
            return new BaseResponse<>(401, null, "获取聊天记录失败");
        }

        return new BaseResponse<>(200, chatDTOList, "获取聊天记录成功");
    }

    /**
     * 发送队伍聊天消息
     */
    @PostMapping("/sendTeamChatMessage")
    public BaseResponse<Boolean> sendTeamChatMessage(@RequestBody SendTeamChatMessageRequest sendTeamChatMessageRequest, HttpServletRequest request) {
        if (sendTeamChatMessageRequest == null) {
            return new BaseResponse<>(500, null, "发送聊天消息请求为空");
        }

        long uid = sendTeamChatMessageRequest.getUid();
        long tid = sendTeamChatMessageRequest.getTid();
        String text = sendTeamChatMessageRequest.getText();
        if (uid <= 0 || tid <= 0 || text == null || text.isEmpty()) {
            return new BaseResponse<>(400, false, "用户id或队伍id或消息内容为空");
        }

        boolean result = chatService.sendTeamChatMessage(uid, tid, text, request);
        if (!result) {
            return new BaseResponse<>(401, false, "发送聊天消息失败");
        }

        return new BaseResponse<>(200, true, "发送聊天消息成功");
    }

}
