package com.codelink.clbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Array;

@Data
public class UpdateUserTags implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long uid;
    private String[] userTags;
}
