package com.codelink.clbackend.config;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;

public class HttpSessionConfig extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake (ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        sec.getUserProperties().put("httpSession", request.getHttpSession());
    }
}
