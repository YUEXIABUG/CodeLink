package com.codelink.clbackend.model.domain.request;

import lombok.Data;

@Data
public class QuitTeamRequest implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private long uid;
    private long pid;
    private long tid;
}
