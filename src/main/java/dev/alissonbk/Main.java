package dev.alissonbk;

import dev.alissonbk.model.Mineracao;
import dev.alissonbk.service.KeyPairGeneratorService;
import dev.alissonbk.service.MineracaoService;
import dev.alissonbk.service.WebSocketService;
import dev.alissonbk.handler.WebSocketSessionHandler;
import dev.alissonbk.service.http.UsuarioClientService;


public class Main {
    private static final KeyPairGeneratorService keyPairGeneratorService = new KeyPairGeneratorService();
    private static final UsuarioClientService usuarioController = new UsuarioClientService();
    private static final WebSocketSessionHandler sessionHandler = new WebSocketSessionHandler();
    private static final WebSocketService webSocketService = new WebSocketService();


    public static void main(String[] args) throws InterruptedException {
        keyPairGeneratorService.generateKeys(); // caso não exista gera chaves e salva no arquivo
        Mineracao mineracao = keyPairGeneratorService.getKeysFromFile(); // pega chaves do arquivo p obj mineracao
        System.out.println("\n");
        MineracaoService mineracaoService = new MineracaoService(mineracao); // instancia mineracao service


        // cadastra usuario caso não exista
        if (usuarioController.createUser(mineracao.getPublicKey())) {
            System.out.println("\n");

            //Connecta websocket
            if (webSocketService.webSocketCreateConnection()) {
                //loop mineração
                mineracaoService.miningLoop();
            }

        }else {
            System.out.println("Falha ao cadastrar usuario");
        }


    }
}
