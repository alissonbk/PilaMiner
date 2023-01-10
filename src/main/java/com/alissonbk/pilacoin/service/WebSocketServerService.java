package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.ResponseMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class WebSocketServerService {

    private final SimpMessagingTemplate messagingTemplate;
    private final UsuarioService usuarioService;

    public WebSocketServerService(SimpMessagingTemplate messagingTemplate, UsuarioService usuarioService) {
        this.messagingTemplate = messagingTemplate;
        this.usuarioService = usuarioService;
    }

    public void notifyPilaMinerado(final String message){
        ResponseMessage response = new ResponseMessage(message);
        //System.out.println(response.getContent());
        //notificationService.sendGlobalNotification();
        messagingTemplate.convertAndSendToUser(
                usuarioService.getLoggedUser().getName(),
                "/topic/pilaminerado", response);
    }


}
