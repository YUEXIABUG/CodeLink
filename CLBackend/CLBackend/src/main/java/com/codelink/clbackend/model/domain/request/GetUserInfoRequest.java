package com.codelink.clbackend.model.domain.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Data
public class GetUserInfoRequest implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long uid;
}
