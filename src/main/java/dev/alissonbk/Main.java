package dev.alissonbk;

import dev.alissonbk.model.Mineracao;
import dev.alissonbk.service.KeyGeneratorService;
import dev.alissonbk.service.MineracaoService;
import dev.alissonbk.service.WebSocketService;
import dev.alissonbk.handler.WebSocketSessionHandler;
import dev.alissonbk.http.UsuarioClientHttp;


public class Main {
    private static final KeyGeneratorService keyGeneratorService = new KeyGeneratorService();
    private static final UsuarioClientHttp usuarioController = new UsuarioClientHttp();
    private static final WebSocketSessionHandler sessionHandler = new WebSocketSessionHandler();
    private static final WebSocketService webSocketService = new WebSocketService();


    public static void main(String[] args) throws InterruptedException {
        keyGeneratorService.generateKeys(); // caso não exista gera chaves e salva no arquivo
        Mineracao mineracao = keyGeneratorService.getKeysFromFile(); // pega chaves do arquivo p obj mineracao
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
