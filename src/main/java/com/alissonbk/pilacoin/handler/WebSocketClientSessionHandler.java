package com.alissonbk.pilacoin.handler;

import com.alissonbk.pilacoin.dto.ValidaCoinRecieveDTO;
import com.alissonbk.pilacoin.model.Mineracao;
import com.alissonbk.pilacoin.service.ValidaCoinService;
import lombok.*;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Objects;


@Service
public class WebSocketClientSessionHandler implements StompSessionHandler {

    private final ValidaCoinService validaCoinService;

    public static BigInteger dificuldade;

    public WebSocketClientSessionHandler(ValidaCoinService validaCoinService) {
        this.validaCoinService = validaCoinService;
    }

    @Override
    public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
        /*dificuldade para minerar pila coin*/
        stompSession.subscribe("/topic/dificuldade", this);
        /*valida mineracao de outros usuarios*/
        stompSession.subscribe("/topic/validaMineracao", this);
        /*descobre novo bloco*/
        stompSession.subscribe("/topic/descobrirNovoBloco", this);
        /*recebe bloco para validar*/
        stompSession.subscribe("/topic/validaBloco", this);
    }

    @Override
    public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) {
        System.out.println("exception:" + throwable +"isConnected? " + stompSession.isConnected() +
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
        if (Objects.equals(stompHeaders.getDestination(), "/topic/descobrirNovoBloco")) {
            return Object.class;
        }
        if (Objects.equals(stompHeaders.getDestination(), "/topic/validaBloco")) {
            return Object.class;
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
            case ("/topic/descobrirNovoBloco") -> handleNovoBloco(o);
            case ("/topic/validaBloco") -> handleValidaBlocoOutroUsuario(o);
        }

    }



    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DificuldadeRet {
        private String dificuldade;
    }

    @SneakyThrows
    private void handleDificuldade(Object o)  {
        dificuldade = new BigInteger(((WebSocketClientSessionHandler.DificuldadeRet) o).getDificuldade(), 16);
        if (!Objects.equals(Mineracao.DIFICULDADE, dificuldade)) {
            System.out.println("Dificuldade Modificada!!!!");
            System.out.println(dificuldade);
        }
        Mineracao.DIFICULDADE = dificuldade;
    }

    private void handleValidacaoPila(Object o) {
        ValidaCoinRecieveDTO pila = (ValidaCoinRecieveDTO) o;
        //System.out.println("ValidaPilaCoinRecieved: " + pila);
        try {
            System.out.println(validaCoinService.validaCoin(pila));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

    }

    private void handleNovoBloco(Object o) {
        System.out.println("Novo bloco: " + o.toString());
    }

    private void handleValidaBlocoOutroUsuario(Object o) {
        System.out.println("Valida bloco: " + o.toString());
    }



}


