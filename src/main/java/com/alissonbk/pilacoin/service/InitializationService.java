package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.http.UsuarioClientHttp;
import com.alissonbk.pilacoin.model.Mineracao;
import com.alissonbk.pilacoin.model.Usuario;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;


@Component
public class InitializationService {

    private final KeyGeneratorService keyGeneratorService;
    private final UsuarioClientHttp usuarioClientHttp;
    private final UsuarioService usuarioService;
    private final WebSocketService webSocketService;
    private final PilaMineradoService pilaMineradoService;

    public InitializationService(KeyGeneratorService keyGeneratorService, UsuarioClientHttp usuarioClientHttp, UsuarioService usuarioService, WebSocketService webSocketService, PilaMineradoService pilaMineradoService) {
        this.keyGeneratorService = keyGeneratorService;
        this.usuarioClientHttp = usuarioClientHttp;
        this.usuarioService = usuarioService;
        this.webSocketService = webSocketService;
        this.pilaMineradoService = pilaMineradoService;
    }

    @PostConstruct
    public void initApp() throws InterruptedException {
        this.keyGeneratorService.generateKeys(); // caso não exista gera chaves e salva no arquivo
        Mineracao mineracao = this.keyGeneratorService.generateMineracaoWithKeys(); // pega chaves do arquivo p obj mineracao
        System.out.println("\n");
        MineracaoService mineracaoService = new MineracaoService(pilaMineradoService, mineracao); // instancia mineracao service

        if ( this.handleUsuario(mineracao.getPublicKey(), mineracao.getPrivateKey()) && this.webSocketService.webSocketCreateConnection() ) {
            mineracaoService.miningLoop();
        } else {
            System.out.println("Falha ao iniciar loop de mineração");
        }

    }

    private boolean handleUsuario(byte[] pubKey, byte[] privateKey) {
        System.out.println("HANDLE USUARIO \n");
        Usuario usuario = new Usuario();
        usuario.setNome(Usuario.NOME);
        usuario.setChavePublica(Base64.getEncoder().encodeToString(pubKey));
        usuario.setChavePrivada(Base64.getEncoder().encodeToString(privateKey));
        usuario.setChavePublicaBytes(pubKey);
        usuario.setChavePrivadaBytes(privateKey);
        usuario.setEmail("alisson@email.com");
        usuario.setPassword("$2a$12$xkgBKv3JxFP/wILLd6j.R.2lZjxa2D.LNfVkwoKJXefbFFcd7XAQK");
        if (usuarioService.saveUser(usuario)) {
            Usuario clientHttpUser = this.usuarioClientHttp.createUser(usuario);
            return clientHttpUser != null;
        }
        return false;
    }
}
