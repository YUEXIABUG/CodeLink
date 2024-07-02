package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友申请表
 * @TableName friends_application
 */
@TableName(value ="friends_application")
@Data
public class FriendsApplication implements Serializable {
    /**
     * 申请id
     */
    @TableId(type = IdType.AUTO)
    private Long aid;

    /**
     * 被申请人uid
     */
    private Long uid;

    /**
     * 申请人uid
     */
    private Long applyUserId;

    /**
     * 申请状态 0-申请中 1-通过 2-拒绝 3-过期
     */
    private Integer applyStatus;

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

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}