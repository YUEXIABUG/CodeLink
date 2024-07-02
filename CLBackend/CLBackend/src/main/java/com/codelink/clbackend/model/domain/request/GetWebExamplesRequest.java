package com.codelink.clbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetWebExamplesRequest implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long wid;
    private String webname;
    private String webDesc;
}
