package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * uid
     */
    @TableId(type = IdType.AUTO)
    private Long uid;

    /**
     * 用户名
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别 0-未知 1-男  2-女
     */
    private Integer userGender;

    /**
     * 年龄
     */
    private Integer userAge;

    /**
     * 地区
     */
    private String userArea;

    /**
     * 电话
     */
    private String userPhone;

    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 标签 json 列表
     */
    private String userTags;

    /**
     * github个人主页
     */
    private String github;

    /**
     * 个人博客
     */
    private String personalWeb;

    /**
     * csdn个人主页
     */
    private String csdn;

    /**
     * 队伍tid列表
     */
    private String teamIds;

    /**
     * 好友uid列表
     */
    private String friendsIds;

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
    @TableLogic
    private Integer isDelete;

    /**
     * 参加的项目pid列表
     */
    private String pid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}