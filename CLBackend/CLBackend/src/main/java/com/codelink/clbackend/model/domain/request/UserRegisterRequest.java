package com.codelink.clbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 5943879662502238378L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
