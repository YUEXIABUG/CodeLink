package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友表
 * @TableName friends
 */
@TableName(value ="friends")
@Data
public class Friends implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private Long uid;

    /**
     * 好友用户id
     */
    private String friendsUid;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}