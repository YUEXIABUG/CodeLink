package com.codelink.clbackend.model.domain.request;

import lombok.Data;

@Data
public class EditProjectInfoRequest implements java.io.Serializable{

    private static final long serialVersionUID = 5943879662502238378L;

    private long uid;
    private long pid;
    private long tid;
    private String projectName;
    private String projectDes;
    private String[] projectTags;
    private String[] needTags;
    private String teamAvatar;
    private int isPersonal;
    private String teamPassword;
}
