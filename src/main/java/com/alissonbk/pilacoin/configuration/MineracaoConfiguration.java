package com.alissonbk.pilacoin.configuration;


import com.alissonbk.pilacoin.model.PilaCoin;
import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe que contém as configurações estaticas para o loop de mineração
 * usada em {@link com.alissonbk.pilacoin.service.MineracaoService}
 * importante que seja setado o par de chaves!
 * */
@Data
public class MineracaoConfiguration {
    public static final long PRINT_TIME_MS = 10000;
    public static boolean MINERACAO_IS_RUNNING = false;
    public static final int FILA_SIZE = 20;
    public static int NUM_MINERACOES = 0;
    public static int NUM_TENTATIVAS = 0;
    public static long TEMPO_INICIAL_TENTATIVA = System.currentTimeMillis();
    public static long TEMPO_INICIAL_MINERACAO = System.currentTimeMillis();
    public static BigInteger DIFICULDADE;
    public static List<PilaCoin> PILA_COINS_REGISTRADOS = new ArrayList<>();
    public static final BlockingQueue<PilaCoin> FILA_COIN = new LinkedBlockingQueue<>(FILA_SIZE);
    public static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    public static byte[] PUB_KEY;
    public static byte[] PRIVATE_KEY;

}
