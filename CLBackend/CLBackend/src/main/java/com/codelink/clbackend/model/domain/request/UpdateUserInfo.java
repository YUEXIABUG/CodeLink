package com.codelink.clbackend.model.domain.request;

import com.codelink.clbackend.model.domain.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserInfo implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long uid;
    private String username;
    private int userGender;
    private int userAge;
    private String userPhone;
    private String userEmail;
    private String github;
    private String personalWeb;
    private String csdn;

//    private User user;
}
