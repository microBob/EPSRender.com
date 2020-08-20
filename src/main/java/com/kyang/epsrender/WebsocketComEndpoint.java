package com.kyang.epsrender;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/communication")
public class WebsocketComEndpoint {
    @OnOpen
    public void OnOpen(Session session) throws IOException {
        // Get session and WebSocket connection
        System.out.println("[Connection Opened]: "+session.getId()+" connected!");
    }

    @OnMessage
    public void OnMessage(String msg, Session session) throws IOException {
        // Handle new Messages
        System.out.println("From: "+session.getId()+"; MSG: "+msg);
    }

    @OnClose
    public void OnClose(Session session) throws IOException {
        // WebSocket connection closes
        System.out.println("[Connection Closed]: "+session.getId()+" left!");
    }

    @OnError
    public void OnError(Session session, Throwable throwable) {
        // Do error handling here
        System.out.println("[WS Error]: "+throwable.getMessage()+" on session "+session.getId());
    }
}
