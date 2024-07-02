package com.codelink.clbackend.DTO;

import com.codelink.clbackend.model.domain.Project;
import com.codelink.clbackend.model.domain.Team;

public class projectListDTO {

    private Project project;
    private Team team;

    public Project getProject() {
        return project;
    }
    public Team getTeam() {
        return team;
    }
    public Long getPid() {
        return project.getPid();
    }
    public String getProjectName() {
        return project.getProjectName();
    }
    public Long getCreateUserId() {
        return project.getCreateUserId();
    }
    public String getProjectTags() {
        return project.getProjectTags();
    }
    public String getNeedTags() {
        return project.getNeedTags();
    }
    public Long getTid() {
        return team.getTid();
    }
    public String getTeamAvatar() {
        return team.getTeamAvatar();
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public void setPid(Long pid) {
        this.project.setPid(pid);
    }
    public void setProjectName(String projectName) {
        this.project.setProjectName(projectName);
    }
    public void setCreateUserId(Long createUserId) {
        this.project.setCreateUserId(createUserId);
    }
    public void setProjectTags(String projectTags) {
        this.project.setProjectTags(projectTags);
    }
    public void setNeedTags(String needTags) {
        this.project.setNeedTags(needTags);
    }
    public void setTid(Long tid) {
        this.team.setTid(tid);
    }
    public void setTeamAvatar(String teamAvatar) {
        this.team.setTeamAvatar(teamAvatar);
    }
}
