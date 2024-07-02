package com.codelink.clbackend.component;

import com.codelink.clbackend.config.HttpSessionConfig;
import com.codelink.clbackend.model.domain.Chat;
import com.codelink.clbackend.model.domain.User;
import com.codelink.clbackend.service.ChatService;
import com.codelink.clbackend.service.UserService;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务
 * 用于实现WebSocket通信
 * 实现功能：
 * 一、私聊功能
 * 1. 用户登录后，将用户的uid和会话保存到sessionMap中
 * 2. 用户发送消息后，将消息发送给指定用户(该用户可不登录)，并将消息保存到chat数据库中
 * 3. 用户退出登录后，将用户的uid和会话从sessionMap中移除
 * 4. 用户登录后，向所有在线用户发送当前在线用户列表
 * 二、群聊功能
 * 1. 用户登录后，将用户的uid和会话保存到sessionMap中
 * 2. 用户发送消息后，将消息发送给指定tid，并将消息保存到chat数据库中
 * 3. 用户退出登录后，将用户的uid和会话从sessionMap中移除
 */
@ServerEndpoint(value = "/chat", configurator = HttpSessionConfig.class)
@Component
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 记录当前在线连接数
     * key - 用户uid , value - 用户会话
     */
    public static final Map<Long, Session> sessionMap = new ConcurrentHashMap<>();
    public static final Gson gson = new Gson();

    HttpSession httpSession;

    @Resource
    UserService userService;
    @Resource
    ChatService chatService;

    /**
     *用户登录太键
     */
    public static final String USER_LOGIN_STATE = "userLoginState";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;

        User LoginUser = (User) httpSession.getAttribute(USER_LOGIN_STATE);
        if (LoginUser == null) {
            log.info("用户未登录");
            return;
        }

        sessionMap.put(LoginUser.getUid(), session);
        log.info("用户登录成功，当前在线人数:{}", LoginUser.getUid(),sessionMap.size());

        Chat chat = new Chat();
        chat.setText(gson.toJson(sessionMap.keySet()));
    }

}
