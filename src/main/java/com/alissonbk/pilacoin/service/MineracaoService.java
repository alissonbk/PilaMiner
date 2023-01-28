package com.alissonbk.pilacoin.service;


import com.alissonbk.pilacoin.http.PilaCoinClientHttp;
import com.alissonbk.pilacoin.configuration.MineracaoConfiguration;
import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.util.Util;
import com.alissonbk.pilacoin.util.UtilGenerators;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Classe responsavel por fazer o looping de mineracao,
 * seu funcionamento depende de {@link MineracaoConfiguration}
 * */
@Service
public class MineracaoService {
    private final TransacaoService transacaoService;
    public static final Logger LOG = Logger.getLogger(MineracaoService.class.getName());
    private final PilaCoinClientHttp pilaCoinClientHttp = new PilaCoinClientHttp();

    public MineracaoService(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }


    public void miningLoop() throws InterruptedException {
        while (MineracaoConfiguration.DIFICULDADE == null) {
            Thread.sleep(500);
        }
        Thread.yield();
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(this.pilaCoinProducer());
        executor.execute(this.pilaCoinConsumer());
        System.out.println("Loop de Mineração iniciado! - Threads: " + MineracaoConfiguration.NUM_THREADS);
    }

    /**
     * Metodo PRODUCER (Gera o magic number ou nonce)
     * */
    private Thread pilaCoinProducer() {
        // PRODUCER
        return new Thread(() -> {
             while (MineracaoConfiguration.MINERACAO_IS_RUNNING) {
                if (MineracaoConfiguration.FILA_COIN.size() == MineracaoConfiguration.FILA_SIZE) {
                    Thread.yield();
                } else {
                    if (MineracaoConfiguration.FILA_COIN.size() < MineracaoConfiguration.FILA_SIZE) {
                        final SecureRandom RANDOM = new SecureRandom();
                        PilaCoin pilaCoin = new PilaCoin();
                        pilaCoin.setDataCriacao(new java.util.Date());
                        pilaCoin.setChaveCriador(Base64.getEncoder().encodeToString(MineracaoConfiguration.PUB_KEY));
                        pilaCoin.setNonceNumber(new BigInteger(128, RANDOM).abs());
                        try {
                            MineracaoConfiguration.FILA_COIN.add(pilaCoin);
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
    private Thread pilaCoinConsumer() {
        return new Thread(() -> {
            while (MineracaoConfiguration.MINERACAO_IS_RUNNING) {
                if (!MineracaoConfiguration.FILA_COIN.isEmpty()) {
                    MineracaoConfiguration.NUM_TENTATIVAS = (MineracaoConfiguration.NUM_TENTATIVAS + 1);
                    try {
                        PilaCoin pilaCoin = MineracaoConfiguration.FILA_COIN.poll();
                        pilaCoin.setNonce(pilaCoin.getNonceNumber().toString());
                        String pilaJson = UtilGenerators.generateJSON(pilaCoin);
                        BigInteger numHash = UtilGenerators.generateHashBigInteger(pilaJson);

                        // Tentativas
                        if (Util.validateMineracao(numHash)) {
                            MineracaoConfiguration.NUM_MINERACOES = MineracaoConfiguration.NUM_MINERACOES + 1;
                            System.out.println("#####################MINEROU######################\n");
                            System.out.println("Número de Mineracoes (Interno): " + MineracaoConfiguration.NUM_MINERACOES);
                            System.out.println("Número de Mineracoes (Registradas): "
                                    + MineracaoConfiguration.PILA_COINS_REGISTRADOS.size());
                            System.out.println("Numero de tentativas: " + MineracaoConfiguration.NUM_TENTATIVAS);
                            System.out.println("Tempo demorado: " +
                                    (System.currentTimeMillis() - MineracaoConfiguration.TEMPO_INICIAL_MINERACAO) + "ms");
                            System.out.println("Numero da Hash gerada: " + numHash);
                            System.out.println("Número da Dificuldade: " + MineracaoConfiguration.DIFICULDADE);
                            System.out.println("Nonce: " + pilaCoin.getNonceNumber());
                            System.out.println("###################################################\n");

                            // Reseta tentativas e tempodemorado
                            MineracaoConfiguration.TEMPO_INICIAL_MINERACAO = System.currentTimeMillis();
                            MineracaoConfiguration.NUM_TENTATIVAS = 0;
                            MineracaoConfiguration.TEMPO_INICIAL_TENTATIVA = System.currentTimeMillis();

                            //Envia pila coin
                            this.sendPilaCoin(pilaJson, pilaCoin);
                        } else {
                            //N MINEROU
                            if (System.currentTimeMillis() - MineracaoConfiguration.TEMPO_INICIAL_TENTATIVA  > MineracaoConfiguration.PRINT_TIME_MS) {
                                System.out.println("---------------Tentando---------------------------");
                                System.out.println("Número de Mineracoes (Interno): " + MineracaoConfiguration.NUM_MINERACOES);
                                System.out.println("Número de Mineracoes (Registradas): "
                                        + MineracaoConfiguration.PILA_COINS_REGISTRADOS.size());
                                System.out.println("Número de tentativas: " + MineracaoConfiguration.NUM_TENTATIVAS);
                                System.out.println("Numero da Hash gerada: " + numHash);
                                System.out.println("Número da Dificuldade: " + MineracaoConfiguration.DIFICULDADE);
                                System.out.println("Tamanho da lista: " + MineracaoConfiguration.FILA_COIN.size());
                                System.out.println("Nonce bit count: " + pilaCoin.getNonceNumber().bitCount());
                                System.out.println("--------------------------------------------------");
                                MineracaoConfiguration.TEMPO_INICIAL_TENTATIVA = System.currentTimeMillis();
                            }
                        }
                    } catch (RuntimeException e) {
                        LOG.warning("Falha ao pegar pila coin da lista");
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                } else {
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
                this.verificaPilaCoin(pilaCoin);
            }
        }catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void verificaPilaCoin(PilaCoin pilaCoin) {
        if (this.pilaCoinClientHttp.verifyPilaCoinExists(pilaCoin)) {
            System.out.println("Pila Coin está cadastrado!");
            transacaoService.savePilaMinerado(pilaCoin);
            MineracaoConfiguration.PILA_COINS_REGISTRADOS.add(pilaCoin);
        } else {
            System.out.println("Falhou... Pila Coin não foi cadastrado!");
        }
    }

    public void startStopLoop() throws InterruptedException {
        MineracaoConfiguration.MINERACAO_IS_RUNNING = !MineracaoConfiguration.MINERACAO_IS_RUNNING;
        if (MineracaoConfiguration.MINERACAO_IS_RUNNING) {
            miningLoop();
        }
    }


}
