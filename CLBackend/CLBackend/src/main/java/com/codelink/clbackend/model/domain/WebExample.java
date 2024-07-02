package com.codelink.clbackend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 网站样例表
 * @TableName web_example
 */
@TableName(value ="web_example")
@Data
public class WebExample implements Serializable {
    /**
     * wid
     */
    @TableId(type = IdType.AUTO)
    private Long wid;

    /**
     * 网站样例名
     */
    private String webname;

    /**
     * 网站简介
     */
    private String webDesc;

    /**
     * 网站预览地址
     */
    private String webSite;

    /**
     * 网站预览图片
     */
    private String webPic;

    /**
     * 网站下载地址
     */
    private String webDownloadSite;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}