package com.alissonbk.pilacoin.handler;

import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.service.UsuarioService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler extends DefaultHandshakeHandler {
    private final UsuarioService usuarioService;

    public UserHandshakeHandler(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Cria o Handshake com o usuario recebendo uma request o websockethandler enviado pelo Stomp no javascript
     * o usuario tem apenas um uuid random para indetificar
     * TODO -> cirar um model para usuario inves de usar o do java security
     * */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return usuarioService.getLoggedUser();
    }
}
