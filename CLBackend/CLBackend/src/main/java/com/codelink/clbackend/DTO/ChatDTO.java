package com.codelink.clbackend.DTO;

import java.util.Date;

public class ChatDTO {

    private long uid;
    private String userAvatar;
    private String username;
    private String projectName;
    private String teamAvatar;
    private long tid;
    private long pid;
    private String text;
    private Date createTime;

    public long getUid() {return uid;}
    public void setUid(long uid) {this.uid = uid;}

    public String getUserAvatar() {return userAvatar;}
    public void setUserAvatar(String userAvatar) {this.userAvatar = userAvatar;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getProjectName() {return projectName;}
    public void setProjectName(String projectName) {this.projectName = projectName;}

    public String getTeamAvatar() {return teamAvatar;}
    public void setTeamAvatar(String teamAvatar) {this.teamAvatar = teamAvatar;}

    public long getTid() {return tid;}
    public void setTid(long tid) {this.tid = tid;}

    public long getPid() {return pid;}
    public void setPid(long pid) {this.pid = pid;}

    public String getText() {return text;}
    public void setText(String text) {this.text = text;}

    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}


}
