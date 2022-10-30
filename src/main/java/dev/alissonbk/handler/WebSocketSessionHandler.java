package dev.alissonbk.handler;

import dev.alissonbk.model.Mineracao;
import lombok.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Objects;


public class WebSocketSessionHandler implements StompSessionHandler {

    public static BigInteger dificuldade;

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        stompSession.subscribe("/topic/dificuldade", this);
    }

    @Override
    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        System.out.println("exception isConnected? " + stompSession.isConnected() +
                " Stomp Coommand: " + stompCommand + " StompHeaders: " + stompHeaders);
    }

    @Override
    public void handleTransportError(StompSession stompSession, Throwable throwable) {
        System.out.println("handleTransportError  StompSession: " + stompSession);
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        if (Objects.equals(stompHeaders.getDestination(), "/topic/dificuldade")) {
            return DificuldadeRet.class;
        }
        return null;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        assert o != null;
        dificuldade = new BigInteger(((DificuldadeRet) o).getDificuldade(), 16);
        if (!Objects.equals(Mineracao.DIFICULDADE, dificuldade)) {
            System.out.println("Dificuldade Modificada!!!!");
            System.out.println(dificuldade);
        }
        Mineracao.DIFICULDADE = dificuldade;

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DificuldadeRet {
        private String dificuldade;
    }

}


