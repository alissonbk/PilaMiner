package dev.alissonbk.service;

import dev.alissonbk.handler.WebSocketSessionHandler;
import dev.alissonbk.util.ServerEndpoints;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Scanner;

@Service
public class WebSocketService {

    private final WebSocketSessionHandler sessionHandler = new WebSocketSessionHandler();

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
