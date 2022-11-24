package dev.alissonbk.service;

import dev.alissonbk.model.Mineracao;
import dev.alissonbk.model.PilaCoin;
import dev.alissonbk.http.PilaCoinClientHttp;
import dev.alissonbk.util.UtilGenerators;
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
    public static final Logger LOG = Logger.getLogger(MineracaoService.class.getName());
    private static final int FILA_SIZE = 20;
    private final BlockingQueue<PilaCoin> FILA_COIN = new LinkedBlockingQueue<>(FILA_SIZE);
    private int vezesPilhaVazia = 0;
    private final int numThreads = Runtime.getRuntime().availableProcessors();
    private Mineracao mineracao;
    private final PilaCoinClientHttp pilaCoinClientHttp = new PilaCoinClientHttp();
    private List<PilaCoin> pilaCoinsRegistrados = new ArrayList<>();

    public MineracaoService(Mineracao mineracao) {
        this.mineracao = mineracao;
    }



    /**
     * Executando forma paralela o numero de tentativas aumentou da média de 280k a cada 20s para 300k a cada 20s
     * */
    public void miningLoop() throws InterruptedException {
        System.out.println("Número total de threads: " + numThreads);
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
             while (true) {
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
            while (true) {
                if (!FILA_COIN.isEmpty()) {
                    mineracao.setNumTentativas(mineracao.getNumTentativas()+1);
                    try {
                        PilaCoin pilaCoin = FILA_COIN.poll();
                        pilaCoin.setNonce(pilaCoin.getNonceNumber().toString());
                        String pilaJson = UtilGenerators.generateJSON(pilaCoin);
                        BigInteger numHash = UtilGenerators.generateHash(pilaJson);

                        // Tentativas
                        if (numHash.compareTo(Mineracao.DIFICULDADE) < 0) {
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
                            System.out.println("Nonce: " + pilaCoin.getNonce());
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
                                //System.out.println("Nonce bit length: " + pilaCoin.getNonceNumber().bitLength());
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
            boolean ok = this.pilaCoinClientHttp.submitPilaCoin(pilaJson);
            if (!ok) {
                System.out.println("Falha ao sumeter pila coin");
            } else {
                this.verificaPilaCoin(pilaCoin);
            }
        }catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void verificaPilaCoin(PilaCoin pilaCoin) {
        boolean ok = this.pilaCoinClientHttp.verifyPilaCoinExists(pilaCoin);
        if (ok) {
            System.out.println("Pila Coin está cadastrado!");
            pilaCoinsRegistrados.add(pilaCoin);
        } else {
            System.out.println("Falhou... Pila Coin não está cadastrado!");
        }
    }

}
