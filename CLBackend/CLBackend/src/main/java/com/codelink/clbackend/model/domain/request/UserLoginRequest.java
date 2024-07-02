package com.codelink.clbackend.model.domain.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Getter
@Setter
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private String userAccount;
    private String userPassword;
}