package com.codelink.clbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.DTO.ChatDTO;
import com.codelink.clbackend.mapper.ProjectMapper;
import com.codelink.clbackend.mapper.TeamMapper;
import com.codelink.clbackend.model.domain.Chat;

import com.codelink.clbackend.model.domain.Team;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.ChatService;
import com.codelink.clbackend.mapper.ChatMapper;
import com.codelink.clbackend.service.ProjectService;
import com.codelink.clbackend.service.TeamService;
import com.codelink.clbackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author yuexiabug
* @description 针对表【chat(聊天消息表)】的数据库操作Service实现
* @createDate 2024-04-13 10:43:41
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements ChatService{

    @Resource
    UserService userService;
    @Resource
    TeamService teamService;
    @Resource
    ProjectService projectService;
    @Resource
    TeamMapper teamMapper;
    @Resource
    ProjectMapper projectMapper;

    @Override
    public List<ChatDTO> getFriendsChatRecord(long uid, long friendUid, HttpServletRequest request) {
        if (uid <= 0 || friendUid <= 0) {
            return null;
        }

        // 查询聊天记录，先查询发送者是uid，接收者是friendUid以及未撤回的记录
        QueryWrapper<Chat> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("fromUid", uid);
        queryWrapper1.eq("toUid", friendUid);
        queryWrapper1.eq("isWithdraw", 0);
        List<Chat> chatList1 = baseMapper.selectList(queryWrapper1);
        // 再查询发送者是friendId，接收者是uid以及未撤回的记录
        QueryWrapper<Chat> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("fromUid", friendUid);
        queryWrapper2.eq("toUid", uid);
        queryWrapper2.eq("isWithdraw", 0);
        List<Chat> chatList2 = baseMapper.selectList(queryWrapper2);
        // 将两个查询结果合并
        chatList1.addAll(chatList2);
        // 将chatList1根据创建时间排序
        chatList1.sort((o1, o2) -> {
            if (o1.getCreateTime().before(o2.getCreateTime())) {
                return -1;
            } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                return 1;
            } else {
                return 0;
            }
        });

        // 将chatList1转换成ChatDTO
        List<ChatDTO> chatDTOList = new ArrayList<>();
        for (Chat chat : chatList1) {
            ChatDTO chatDTO = new ChatDTO();
            chatDTO.setText(chat.getText());
            chatDTO.setCreateTime(chat.getCreateTime());
            chatDTO.setUid(chat.getFromUid());

            // 根据uid查询用户信息
            User user = userService.getUserInfo(chat.getFromUid(), request);
            if (user != null) {
                chatDTO.setUserAvatar(user.getUserAvatar());
                chatDTO.setUsername(user.getUsername());
            }

            chatDTOList.add(chatDTO);

            // 将isRead设置为1
            chat.setIsRead(1);
            updateById(chat);
        }
        return chatDTOList;
    }

    @Override
    public boolean sendFriendsChatMessage(long uid, long friendUid, String text, HttpServletRequest request) {
        if (uid <= 0 || friendUid <= 0) {
            return false;
        }

        Chat chat = new Chat();
        chat.setFromUid(uid);
        chat.setToUid(friendUid);
        chat.setText(text);
        chat.setChatType(1);
        return save(chat);
    }

    @Override
    public boolean withdrawChatMessage(long chatId, long uid, HttpServletRequest request) {
        if (chatId <= 0 || uid <= 0) {
            return false;
        }

        Chat chat = getById(chatId);
        if (chat == null) {
            return false;
        }

        if (chat.getFromUid() != uid) {
            return false;
        }

        chat.setIsWithdraw(1);
        return updateById(chat);
    }

    @Override
    public List<ChatDTO> getTeamChatRecord(long uid, long tid, HttpServletRequest request) {
        if (uid <= 0 || tid <= 0) {
            return null;
        }

        // 查询聊天记录，获得tid对应的聊天记录
        QueryWrapper<Chat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tid", tid);
        queryWrapper.eq("isWithdraw", 0);
        List<Chat> chatList = baseMapper.selectList(queryWrapper);
        // 将chatList根据创建时间排序
        chatList.sort((o1, o2) -> {
            if (o1.getCreateTime().before(o2.getCreateTime())) {
                return -1;
            } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                return 1;
            } else {
                return 0;
            }
        });

        // 将chatList转换成ChatDTO
        List<ChatDTO> chatDTOList = new ArrayList<>();
        for (Chat chat : chatList) {
            ChatDTO chatDTO = new ChatDTO();
            chatDTO.setText(chat.getText());
            chatDTO.setCreateTime(chat.getCreateTime());
            chatDTO.setUid(chat.getFromUid());
            chatDTO.setTid(chat.getTid());

            // 根据tid在team表中查找pid
            QueryWrapper<Team> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("tid", tid);
            Team team = teamService.getOne(queryWrapper1);
            long pid = team.getPid();
            chatDTO.setPid(pid);

            // 根据pid和tid查询projectName和teamName
            String projectName = projectMapper.selectById(pid).getProjectName();
            String teamAvatar = teamMapper.selectById(tid).getTeamAvatar();
            chatDTO.setProjectName(projectName);
            chatDTO.setTeamAvatar(teamAvatar);

            // 根据uid查询用户信息
            User user = userService.getUserInfo(chat.getFromUid(), request);
            if (user != null) {
                chatDTO.setUserAvatar(user.getUserAvatar());
                chatDTO.setUsername(user.getUsername());
            }

            chatDTOList.add(chatDTO);

            // 将isRead设置为1
            chat.setIsRead(1);
            updateById(chat);
        }

        return chatDTOList;
    }

    @Override
    public boolean sendTeamChatMessage(long uid, long tid, String text, HttpServletRequest request) {
        if (uid <= 0 || tid <= 0) {
            return false;
        }

        Chat chat = new Chat();
        chat.setFromUid(uid);
        chat.setTid(tid);
        chat.setText(text);
        chat.setChatType(2);
        return save(chat);
    }
}




