package dev.alissonbk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alissonbk.model.Mineracao;
import dev.alissonbk.model.PilaCoin;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * USAR ABS para valor absoluto no BigInteger (onde tem random utilizar .abs())
 */
@Service
public class MineracaoService {
    public static final Logger LOG = Logger.getLogger(MineracaoService.class.getName());
    // private final Semaphore SEMAFORO_PRODUCER = new Semaphore((this.numThreads*20)/100);
    // private final Semaphore SEMAFORO_CONSUMER = new Semaphore((this.numThreads*80)/100);
    private static final int FILA_SIZE = 20;
    private final BlockingQueue<PilaCoin> FILA_COIN = new LinkedBlockingQueue<>(FILA_SIZE);
    private int vezesPilhaVazia = 0;
    private final int numThreads = Runtime.getRuntime().availableProcessors();
    private Mineracao mineracao;

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
     * Este metodo gera o Magic Number logo é lento...
     * Metodo PRODUCER
     * */
    private Thread pilaCoinProducer(Mineracao mineracao) {
        // PRODUCER
        return new Thread(() -> {
             while (true) {
                if (FILA_COIN.size() == FILA_SIZE) {
                    Thread.yield();
                } else {
                    if (FILA_COIN.size() < FILA_SIZE) {
                        // this.producerAquire();
                        final SecureRandom RANDOM = new SecureRandom();
                        PilaCoin pilaCoin = new PilaCoin();
                        pilaCoin.setDataCriacao(Date.from(Instant.now()));
                        pilaCoin.setIdCriador("alisson");
                        pilaCoin.setChaveCriador(mineracao.getPublicKey());
                        pilaCoin.setNonce(new BigInteger(128, RANDOM).abs());
                        try {
                            FILA_COIN.add(pilaCoin);
                        } catch (RuntimeException e) {
                            LOG.warning("Falha ao adicionar pila coin a fila!");
                            throw new RuntimeException(e);
                        } finally {
                            //  SEMAFORO_PRODUCER.release();
                        }
                    }
                }
            }
        });
    }

    /**
     *
     * */
    private Thread pilaCoinConsumer(Mineracao mineracao) {
        return new Thread(() -> {
            while (true) {
                if (!FILA_COIN.isEmpty()) {
                    mineracao.setNumTentativas(mineracao.getNumTentativas()+1);
                    // consumerAquire();
                    try {
                        PilaCoin pilaCoin = FILA_COIN.poll();
                        String pilaJson = this.generateJSON(pilaCoin);
                        BigInteger numHash = new BigInteger(this.generateHash(pilaJson)).abs();

                        // Tentativas
                        if (numHash.compareTo(Mineracao.DIFICULDADE) < 0) {
                            mineracao.setNumMineracoes(mineracao.getNumMineracoes() + 1);
                            System.out.println("#####################MINEROU######################\n");
                            System.out.println("Numero de Mineracoes: " + mineracao.getNumMineracoes());
                            System.out.println("Numero de tentativas: " + mineracao.getNumTentativas());
                            System.out.println("Tempo demorado: " +
                                    (System.currentTimeMillis() - mineracao.getTempoInicialMineracao()) + "ms");
                            System.out.println("Numero da Hash gerada: " + numHash);
                            System.out.println("Número da Dificuldade: " + Mineracao.DIFICULDADE);
                            System.out.println("###################################################\n");

                            // Reseta tentativas e tempodemorado
                            mineracao.setTempoInicialMineracao(System.currentTimeMillis());
                            mineracao.setNumTentativas(0);
                            mineracao.setTempoInicialTentativa(System.currentTimeMillis());
                        } else {
                            //N MINEROU
                            if (System.currentTimeMillis() - mineracao.getTempoInicialTentativa() > mineracao.getPRINT_TIME_MS()) {
                                System.out.println("---------------Tentando----------------");
                                System.out.println("Número de Mineracoes: " + mineracao.getNumMineracoes());
                                System.out.println("Número de tentativas: " + mineracao.getNumTentativas());
                                System.out.println("Numero da Hash gerada: " + numHash);
                                System.out.println("Número da Dificuldade: " + Mineracao.DIFICULDADE);
                                System.out.println("Tamanho da lista: " + FILA_COIN.size());
                                System.out.println("Veses Fila vazia: "+ vezesPilhaVazia);
                                System.out.println("---------------------------------------");
                                mineracao.setTempoInicialTentativa(0);
                                mineracao.setTempoInicialTentativa(System.currentTimeMillis());
                            }
                        }
                    } catch (RuntimeException e) {
                        LOG.warning("Falha ao pegar pila coin da lista");
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }finally {
                        // SEMAFORO_CONSUMER.release();
                    }
                } else {
                    vezesPilhaVazia++;
                    Thread.yield();
                }
            }

        });

    }

    private String generateJSON(PilaCoin pilaCoin) {
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(pilaCoin);
        } catch (JsonProcessingException e) {
            LOG.warning("Falha ao gerar JSON do pila coin!");
            e.printStackTrace();
        }

        if (json.equals("")) {
            throw new RuntimeException("JSON ficou como uma String vazia!");
        }
        return json;
    }

    private byte[] generateHash(String pilaJson) {
        byte[] hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(pilaJson.getBytes("UTF-8"));
        }catch (NoSuchAlgorithmException e) {
            LOG.warning("Algoritmo para gerar HASH incorreto!");
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            LOG.warning("Falha ao gerar HASH do JSON");
            e.printStackTrace();
        }

        if (hash == null) {
            throw new RuntimeException("Falha ao gerar HASH (a hash gerada é null)");
        }
        return hash;
    }

//    private void producerAquire() {
//        try {
//            SEMAFORO_PRODUCER.acquire();
//        }catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

//    private void consumerAquire() {
//        try {
//            SEMAFORO_CONSUMER.acquire();
//        }catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }

}
