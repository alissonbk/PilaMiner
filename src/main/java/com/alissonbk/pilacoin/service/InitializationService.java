package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.http.UsuarioClientHttp;
import com.alissonbk.pilacoin.model.Mineracao;
import com.alissonbk.pilacoin.model.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
public class InitializationService {

    private final KeyGeneratorService keyGeneratorService;
    private final UsuarioClientHttp usuarioClientHttp;
    private final UsuarioService usuarioService;
    private final WebSocketService webSocketService;

    public InitializationService(KeyGeneratorService keyGeneratorService, UsuarioClientHttp usuarioClientHttp, UsuarioService usuarioService, WebSocketService webSocketService) {
        this.keyGeneratorService = keyGeneratorService;
        this.usuarioClientHttp = usuarioClientHttp;
        this.usuarioService = usuarioService;
        this.webSocketService = webSocketService;
    }

    @PostConstruct
    public void initApp() throws InterruptedException {
        keyGeneratorService.generateKeys(); // caso não exista gera chaves e salva no arquivo
        Mineracao mineracao = keyGeneratorService.generateMineracaoWithKeys(); // pega chaves do arquivo p obj mineracao
        System.out.println("\n");
        MineracaoService mineracaoService = new MineracaoService(mineracao); // instancia mineracao service

        Usuario usuario = usuarioClientHttp.createUser(mineracao.getPublicKey());
        // cadastra usuario caso não exista
        if (usuario != null) {
            System.out.println("\n");
            usuarioService.saveUser(usuario);

            if (webSocketService.webSocketCreateConnection()) {
                //loop mineração
                mineracaoService.miningLoop();

            } else {
                System.out.println("Falha ao cadastrar usuario");
            }
        }
    }
}
