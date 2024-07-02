package com.codelink.clbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetWebExampleInfoRequest implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long wid;
    private String webname;
    private String webDesc;
    private String webSite;
    private String webPic;
    private String webDownloadSite;
}
