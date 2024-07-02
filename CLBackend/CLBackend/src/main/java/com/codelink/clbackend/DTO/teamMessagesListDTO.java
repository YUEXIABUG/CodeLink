package com.codelink.clbackend.DTO;

import com.codelink.clbackend.model.domain.Project;
import com.codelink.clbackend.model.domain.Team;
import com.codelink.clbackend.model.domain.TeamMessage;
import com.codelink.clbackend.model.domain.User;

public class teamMessagesListDTO {
    private Project project;
    private Team team;
    private User user;
    private TeamMessage teamMessage;

    public Project getProject() {
        return project;
    }
    public Team getTeam() {
        return team;
    }
    public User getUser() {
        return user;
    }
    public TeamMessage getTeamMessage() {
        return teamMessage;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setTeamMessage(TeamMessage teamMessage) {
        this.teamMessage = teamMessage;
    }
}
