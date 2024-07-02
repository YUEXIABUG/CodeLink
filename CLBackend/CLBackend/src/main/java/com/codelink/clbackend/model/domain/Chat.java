package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息表
 * @TableName chat
 */
@TableName(value ="chat")
@Data
public class Chat implements Serializable {
    /**
     * 聊天记录id
     */
    @TableId(type = IdType.AUTO)
    private Long chatId;

    /**
     * 发送消息Uid
     */
    private Long fromUid;

    /**
     * 接收消息Uid
     */
    private Long toUid;

    /**
     * 内容
     */
    private String text;

    /**
     * 聊天类型 1-私聊 2-群聊
     */
    private Integer chatType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 队伍聊天时的队伍id
     */
    private Long tid;

    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 是否撤回 0-未撤回 1-已撤回
     */
    private Integer isWithdraw;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}