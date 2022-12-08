package dev.alissonbk.handler;

import dev.alissonbk.model.Mineracao;
import dev.alissonbk.dto.recieve.ValidaCoinRecieveDTO;
import dev.alissonbk.service.ValidaCoinService;
import lombok.*;
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
            return ValidaCoinRecieveDTO.class;
        }

        return null;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        assert o != null;
        assert stompHeaders.getDestination() != null;
        switch (stompHeaders.getDestination()) {
            case ("/topic/dificuldade") -> handleDificuldade(o);
            case ("/topic/validaMineracao") -> handleValidacaoPila(o);

        }

    }



    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DificuldadeRet {
        private String dificuldade;
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
        ValidaCoinRecieveDTO pila = (ValidaCoinRecieveDTO) o;
        System.out.println("ValidaPilaCoinRecieved: " + pila);
        try {
            System.out.println(ValidaCoinService.validaCoin(pila));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }



}


