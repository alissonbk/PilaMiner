package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.ResponseMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServerService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketServerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyPilaMinerado(final String message){
        ResponseMessage response = new ResponseMessage(message);
        messagingTemplate.convertAndSend("/topic/mineracaoPila", response);
    }

    public void notifyValidacaoPila(final String message){
        ResponseMessage response = new ResponseMessage(message);
        messagingTemplate.convertAndSend("/topic/validacaoPilaBloco", response);
    }


}
