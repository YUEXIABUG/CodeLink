package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目表
 * @TableName project
 */
@TableName(value ="project")
@Data
public class Project implements Serializable {
    /**
     * pid
     */
    @TableId(type = IdType.AUTO)
    private Long pid;

    /**
     * 项目发起人uid
     */
    private Long createUserId;

    /**
     * 对应队伍的tid
     */
    private Long tid;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目简介
     */
    private String projectDes;

    /**
     * 项目标签
     */
    private String projectTags;

    /**
     * 项目所需标签
     */
    private String needTags;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否已经完成
     */
    private Integer isFinish;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}