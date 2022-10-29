package dev.alissonbk.service;

import dev.alissonbk.handler.WebSocketSessionHandler;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Scanner;

@Service
public class WebSocketService {

    public void webSocketCreateConnection() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new StringMessageConverter());
        StompSessionHandler sessionHandler = new WebSocketSessionHandler();
        stompClient.connect("wss://srv-ceesp.proj.ufsm.br:8097/websocket/websocket", sessionHandler);
        //stompClient.connect("ws://192.168.81.101r:8080/websocket/websocket", sessionHandler);
        new Scanner(System.in).nextLine();
    }

}
