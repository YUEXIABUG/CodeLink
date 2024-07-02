package com.codelink.clbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class SetFriendsApplicationStatusRequest implements Serializable {

    private static final long serialVersionUID = 5943879662502238378L;

    private long aid;
    private int applyStatus;
}