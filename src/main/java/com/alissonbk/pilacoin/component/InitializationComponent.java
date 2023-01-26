package com.alissonbk.pilacoin.component;

import com.alissonbk.pilacoin.http.UsuarioClientHttp;
import com.alissonbk.pilacoin.configuration.MineracaoConfiguration;
import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.service.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;


@Component
public class InitializationComponent {

    private final KeyGeneratorService keyGeneratorService;
    private final UsuarioClientHttp usuarioClientHttp;
    private final UsuarioService usuarioService;
    private final WebSocketClientService webSocketClientService;

    public InitializationComponent(KeyGeneratorService keyGeneratorService, UsuarioClientHttp usuarioClientHttp,
                                   UsuarioService usuarioService, WebSocketClientService webSocketClientService) {
        this.keyGeneratorService = keyGeneratorService;
        this.usuarioClientHttp = usuarioClientHttp;
        this.usuarioService = usuarioService;
        this.webSocketClientService = webSocketClientService;
    }

    @PostConstruct
    public void initApp() throws InterruptedException {
        this.keyGeneratorService.generateKeys(); // caso não exista gera par de chaves e salva no arquivo
        MineracaoConfiguration.PUB_KEY = KeyGeneratorService.getPublicKeyBytes();
        MineracaoConfiguration.PRIVATE_KEY = KeyGeneratorService.getPrivateKeyBytes();

        if (!this.handleUsuario(MineracaoConfiguration.PUB_KEY,  MineracaoConfiguration.PRIVATE_KEY)
                || !this.webSocketClientService.webSocketCreateConnection()) {
            System.out.println("\nFalha ao iniciar loop de mineração");
        }

    }

    private boolean handleUsuario(byte[] pubKey, byte[] privateKey) {
        System.out.println("\nHANDLE USUARIO \n");
        Usuario usuario = new Usuario();
        usuario.setNome(Usuario.NOME);
        usuario.setChavePublica(Base64.getEncoder().encodeToString(pubKey));
        usuario.setChavePrivada(Base64.getEncoder().encodeToString(privateKey));
        usuario.setChavePublicaBytes(pubKey);
        usuario.setChavePrivadaBytes(privateKey);
        usuario.setEmail("alisson@email.com");
        usuario.setPassword("$2a$12$xkgBKv3JxFP/wILLd6j.R.2lZjxa2D.LNfVkwoKJXefbFFcd7XAQK");


        if (!usuarioService.verifyUserExistsOnDB(usuario)) {
            if (usuarioService.saveUser(usuario)) {
                Usuario clientHttpUser = this.usuarioClientHttp.createUser(usuario);
                return clientHttpUser != null;
            }
        }else return true;

        return false;
    }
}
