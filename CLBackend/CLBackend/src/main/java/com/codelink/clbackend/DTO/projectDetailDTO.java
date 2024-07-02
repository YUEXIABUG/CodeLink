package com.codelink.clbackend.DTO;

import com.codelink.clbackend.model.domain.Project;
import com.codelink.clbackend.model.domain.Team;

public class projectDetailDTO {

    private Project project;
    private Team team;

    public Project getProject() {
        return project;
    }
    public Team getTeam() {
        return team;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public void setTeam(Team team) {
        this.team = team;
    }
}
