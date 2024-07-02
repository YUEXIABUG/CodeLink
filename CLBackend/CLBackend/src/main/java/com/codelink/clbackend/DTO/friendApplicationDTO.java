package com.codelink.clbackend.DTO;

import com.codelink.clbackend.model.domain.User;

public class friendApplicationDTO {
    private User user;
    private long aid;
    private int applyStatus;
    private String remark;

    public User getUser() {
        return user;
    }
    public long getAid() {
        return aid;
    }
    public Integer getApplyStatus() {
        return applyStatus;
    }
    public String getRemark() {
        return remark;
    }
    public Long getUid() {
        return user.getUid();
    }
    public String getUsername() {
        return user.getUsername();
    }
    public String getUserAvatar() {
        return user.getUserAvatar();
    }
    public String getUserTags() {
        return user.getUserTags();
    }


    public void setUser(User friend) {
        this.user = friend;
    }
    public void setAid(Long aid) {
        this.aid = aid;
    }
    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public void setUid(Long uid) {
        this.user.setUid(uid);
    }
    public void setUsername(String username) {
        this.user.setUsername(username);
    }
    public void setUserAvatar(String userAvatar) {
        this.user.setUserAvatar(userAvatar);
    }

    public void setUserTags(String userTags) {
        this.user.setUserTags(userTags);
    }
}
