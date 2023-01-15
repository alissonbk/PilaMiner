package com.alissonbk.pilacoin.service;


import com.alissonbk.pilacoin.http.PilaCoinClientHttp;
import com.alissonbk.pilacoin.model.Mineracao;
import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.util.Util;
import com.alissonbk.pilacoin.util.UtilGenerators;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * USAR ABS para valor absoluto no BigInteger (onde tem random utilizar .abs())
 */
@Service
public class MineracaoService {
    private TransacaoService transacaoService;
    public static final Logger LOG = Logger.getLogger(MineracaoService.class.getName());
    private static final int FILA_SIZE = 20;
    private final BlockingQueue<PilaCoin> FILA_COIN = new LinkedBlockingQueue<>(FILA_SIZE);
    private int vezesPilhaVazia = 0;
    private final int numThreads = Runtime.getRuntime().availableProcessors();
    private Mineracao mineracao;
    private final PilaCoinClientHttp pilaCoinClientHttp = new PilaCoinClientHttp();
    private List<PilaCoin> pilaCoinsRegistrados = new ArrayList<>();
    public static boolean MINERACAO_IS_RUNNING = false;


    public MineracaoService(TransacaoService transacaoService, Mineracao mineracao){
        this.transacaoService = transacaoService;
        this.mineracao = mineracao;
    }
    public MineracaoService() {

    }


    /**
     * Executando forma paralela o numero de tentativas aumentou da média de 280k a cada 20s para 300k a cada 20s
     * */
    public void miningLoop() throws InterruptedException {
        System.out.println("Loop de Mineração iniciado! - Threads: " + numThreads);
        while (Mineracao.DIFICULDADE == null) {
            Thread.sleep(500);
        }
        Thread.yield();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(this.pilaCoinProducer(this.mineracao));
        executor.execute(this.pilaCoinConsumer(this.mineracao));
    }

    /**
     * Metodo PRODUCER (Gera o magic number ou nonce)
     * */
    private Thread pilaCoinProducer(Mineracao mineracao) {
        // PRODUCER
        return new Thread(() -> {
             while (MINERACAO_IS_RUNNING) {
                if (FILA_COIN.size() == FILA_SIZE) {
                    Thread.yield();
                } else {
                    if (FILA_COIN.size() < FILA_SIZE) {
                        final SecureRandom RANDOM = new SecureRandom();
                        PilaCoin pilaCoin = new PilaCoin();
                        pilaCoin.setDataCriacao(new java.util.Date());
                        pilaCoin.setChaveCriador(Base64.getEncoder().encodeToString(mineracao.getPublicKey()));
                        pilaCoin.setNonceNumber(new BigInteger(128, RANDOM).abs());
                        try {
                            FILA_COIN.add(pilaCoin);
                        } catch (RuntimeException e) {
                            LOG.warning("Falha ao adicionar pila coin a fila!");
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }

    /**
     * CONSUMER (gera um hash do pila coin e verifica se é valido)
     * */
    private Thread pilaCoinConsumer(Mineracao mineracao) {
        return new Thread(() -> {
            while (MINERACAO_IS_RUNNING) {
                if (!FILA_COIN.isEmpty()) {
                    mineracao.setNumTentativas(mineracao.getNumTentativas()+1);
                    try {
                        PilaCoin pilaCoin = FILA_COIN.poll();
                        pilaCoin.setNonce(pilaCoin.getNonceNumber().toString());
                        String pilaJson = UtilGenerators.generateJSON(pilaCoin);
                        BigInteger numHash = UtilGenerators.generateHash(pilaJson);

                        // Tentativas
                        if (Util.validateMineracao(numHash)) {
                            mineracao.setNumMineracoes(mineracao.getNumMineracoes() + 1);
                            System.out.println("#####################MINEROU######################\n");
                            System.out.println("Número de Mineracoes (Interno): " + mineracao.getNumMineracoes());
                            System.out.println("Número de Mineracoes (Registradas): "
                                    + this.pilaCoinsRegistrados.size());
                            System.out.println("Numero de tentativas: " + mineracao.getNumTentativas());
                            System.out.println("Tempo demorado: " +
                                    (System.currentTimeMillis() - mineracao.getTempoInicialMineracao()) + "ms");
                            System.out.println("Numero da Hash gerada: " + numHash);
                            System.out.println("Número da Dificuldade: " + Mineracao.DIFICULDADE);
                            System.out.println("Nonce: " + pilaCoin.getNonceNumber());
                            System.out.println("###################################################\n");

                            // Reseta tentativas e tempodemorado
                            mineracao.setTempoInicialMineracao(System.currentTimeMillis());
                            mineracao.setNumTentativas(0);
                            mineracao.setTempoInicialTentativa(System.currentTimeMillis());

                            //Envia pila coin
                            this.sendPilaCoin(pilaJson, pilaCoin);
                        } else {
                            //N MINEROU
                            if (System.currentTimeMillis() - mineracao.getTempoInicialTentativa() > Mineracao.PRINT_TIME_MS) {
                                System.out.println("---------------Tentando---------------------------");
                                System.out.println("Número de Mineracoes (Interno): " + mineracao.getNumMineracoes());
                                System.out.println("Número de Mineracoes (Registradas): "
                                        + this.pilaCoinsRegistrados.size());
                                System.out.println("Número de tentativas: " + mineracao.getNumTentativas());
                                System.out.println("Numero da Hash gerada: " + numHash);
                                System.out.println("Número da Dificuldade: " + Mineracao.DIFICULDADE);
                                System.out.println("Tamanho da lista: " + FILA_COIN.size());
                                System.out.println("Veses Fila vazia: "+ vezesPilhaVazia);
                                System.out.println("Nonce bit count: " + pilaCoin.getNonceNumber().bitCount());
                                System.out.println("--------------------------------------------------");
                                mineracao.setTempoInicialTentativa(0);
                                mineracao.setTempoInicialTentativa(System.currentTimeMillis());
                            }
                        }
                    } catch (RuntimeException e) {
                        LOG.warning("Falha ao pegar pila coin da lista");
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                } else {
                    vezesPilhaVazia++;
                    Thread.yield();
                }
            }

        });
    }

    private void sendPilaCoin(String pilaJson, PilaCoin pilaCoin) {
        try {
            if (!this.pilaCoinClientHttp.submitPilaCoin(pilaJson)) {
                System.out.println("Falha ao sumeter pila coin");
            } else {
                this.verificaPilaCoin(pilaCoin, pilaJson);
            }
        }catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void verificaPilaCoin(PilaCoin pilaCoin, String pilaJson) {
        if (this.pilaCoinClientHttp.verifyPilaCoinExists(pilaCoin)) {
            System.out.println("Pila Coin está cadastrado!");
            transacaoService.savePilaMinerado(pilaCoin);
            pilaCoinsRegistrados.add(pilaCoin);
        } else {
            System.out.println("Falhou... Pila Coin não está cadastrado!");
        }
    }

    public void startStopLoop(Usuario usuario, TransacaoService transacaoService) throws InterruptedException {
        MineracaoService.MINERACAO_IS_RUNNING = !MineracaoService.MINERACAO_IS_RUNNING;
        if (MineracaoService.MINERACAO_IS_RUNNING) {
            this.transacaoService = transacaoService;
            this.mineracao = new Mineracao(usuario.getChavePublicaBytes(), usuario.getChavePrivadaBytes());
            miningLoop();
        }
    }


}
