package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍消息表
 * @TableName team_message
 */
@TableName(value ="team_message")
@Data
public class TeamMessage implements Serializable {
    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Long nid;

    /**
     * 接收人uid
     */
    private Long recipientUid;

    /**
     * 被申请队伍tid
     */
    private Long tid;

    /**
     * 队长uid
     */
    private Long createUserId;

    /**
     * 申请人uid
     */
    private Long applyUserId;

    /**
     * 加入申请 0-申请中 1-通过 2-拒绝 3-不是此类消息
     */
    private Integer applyMessage;

    /**
     * 退出消息 0-不是此类消息 1-是此类消息
     */
    private Integer quitMessage;

    /**
     * 解散消息 0-不是此类消息 1-是此类消息
     */
    private Integer breakMessage;

    /**
     * 完成消息 0-不是此类消息 1-是此类消息
     */
    private Integer finishedMessage;

    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer isRead;

    /**
     * 申请备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}