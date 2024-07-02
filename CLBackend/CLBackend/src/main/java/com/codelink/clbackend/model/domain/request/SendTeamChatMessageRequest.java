package com.codelink.clbackend.model.domain.request;

import lombok.Data;

@Data
public class SendTeamChatMessageRequest implements java.io.Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long uid;
    private long tid;
    private String text;
}
