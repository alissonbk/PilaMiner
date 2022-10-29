package dev.alissonbk;

import dev.alissonbk.service.http.UsuarioClientService;
import dev.alissonbk.model.Mineracao;
import dev.alissonbk.service.KeyPairGeneratorService;
import dev.alissonbk.service.MineracaoService;
import dev.alissonbk.service.WebSocketService;
import dev.alissonbk.handler.WebSocketSessionHandler;

public class Main {
    private static KeyPairGeneratorService keyPairGeneratorService = new KeyPairGeneratorService();
    private static UsuarioClientService usuarioController = new UsuarioClientService();
    private static WebSocketSessionHandler sessionHandler = new WebSocketSessionHandler();
    private static WebSocketService webSocketService = new WebSocketService(sessionHandler);


    public static void main(String[] args) {
        keyPairGeneratorService.generateKeys(); // caso não exista gera chaves e salva no arquivo
        Mineracao mineracao = keyPairGeneratorService.getKeysFromFile(); // pega chaves do arquivo p obj mineracao
        System.out.println("\n");
        MineracaoService mineracaoService = new MineracaoService(mineracao); // instancia mineracao service


        // cadastra usuario caso não exista
        if (usuarioController.createUser(mineracao.getPublicKey())) {
            //Connecta websocket
            //webSocketService.webSocketCreateConnection();

            //loop mineração
            // mineracaoService.miningLoop();
        }else {
            System.out.println("Falha ao cadastrar usuario");
        }


    }
}
