package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍表
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team implements Serializable {
    /**
     * tid
     */
    @TableId(type = IdType.AUTO)
    private Long tid;

    /**
     * 队伍对应的pid
     */
    private Long pid;

    /**
     * 队长uid
     */
    private Long createUserId;

    /**
     * 队员id
     */
    private String usersId;

    /**
     * 队伍头像
     */
    private String teamAvatar;

    /**
     * 是否是私人队伍
     */
    private Integer isPersonal;

    /**
     * 队伍密码
     */
    private String teamPassword;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 是否满员 0-未满员 1-满员
     */
    private Integer isFull;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}