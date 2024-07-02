package com.codelink.clbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateNewProjectRequest implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long uid;
    private String projectName;
    private String projectDes;
    private String[] projectTags;
    private String[] needTags;
    private String teamAvatar;
    private int isPersonal;
    private String teamPassword;
}
