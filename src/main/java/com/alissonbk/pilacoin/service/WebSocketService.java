package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.handler.WebSocketSessionHandler;
import com.alissonbk.pilacoin.util.ServerEndpoints;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Service
public class WebSocketService {
    private final WebSocketSessionHandler sessionHandler;

    public WebSocketService(WebSocketSessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }



    public Boolean webSocketCreateConnection() {
        try {
            StandardWebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            stompClient.connect(ServerEndpoints.WEBSOCKET, sessionHandler);
            return true;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }


}
