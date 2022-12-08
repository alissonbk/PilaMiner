package dev.alissonbk.handler;

import dev.alissonbk.model.Mineracao;
import dev.alissonbk.model.PilaCoin;
import dev.alissonbk.model.PilaValidaRet;
import lombok.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.Buffer;
import java.util.Base64;
import java.util.Objects;


public class WebSocketSessionHandler implements StompSessionHandler {

    public static BigInteger dificuldade;

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        /*dificuldade para minerar pila coin*/
        stompSession.subscribe("/topic/dificuldade", this);
        /*valida mineracao de outros usuarios*/
        stompSession.subscribe("/topic/validaMineracao", this);
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
        assert stompHeaders.getDestination() != null;
        if (Objects.equals(stompHeaders.getDestination(), "/topic/dificuldade")) {
            return DificuldadeRet.class;
        }
        if (Objects.equals(stompHeaders.getDestination(), "/topic/validaMineracao")) {
            return PilaValidaRet.class;
        }

        return null;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        assert o != null;
        assert stompHeaders.getDestination() != null;
        switch (stompHeaders.getDestination()) {
            case("/topic/dificuldade"):
                handleDificuldade(o);
            break;
            case("/topic/validaMineracao"):
                System.out.println("Stomp Headers: " + stompHeaders.toString());
                handleValidacaoPila(o);
            break;
        }

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DificuldadeRet {
        private String dificuldade;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidacaoRet {
        private String validacao;
    }

    private void handleDificuldade(Object o) {
        dificuldade = new BigInteger(((WebSocketSessionHandler.DificuldadeRet) o).getDificuldade(), 16);
        if (!Objects.equals(Mineracao.DIFICULDADE, dificuldade)) {
            System.out.println("Dificuldade Modificada!!!!");
            System.out.println(dificuldade);
        }
        Mineracao.DIFICULDADE = dificuldade;
    }

    private void handleValidacaoPila(Object o) {
        PilaValidaRet pilaValidaRet = (PilaValidaRet) o;
        System.out.println("PilaValidaRet: " + pilaValidaRet);
    }



}


