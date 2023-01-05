package com.alissonbk.pilacoin.model;


import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Data
public class Mineracao {

    private Long id;
    private int numMineracoes = 0;
    private int numTentativas = 0;
    private long tempoInicialTentativa = System.currentTimeMillis();
    private long tempoInicialMineracao = System.currentTimeMillis();
   // private final String HEXSTRING;
    public static BigInteger DIFICULDADE;
    private final byte[] publicKey;
    private final byte[] privateKey;
    public static final long PRINT_TIME_MS = 10000;

    public Mineracao(byte[] publicKey, byte[] privateKey) {
        //HEXSTRING = this.geraF(58);
        //DIFICULDADE = new BigInteger(HEXSTRING, 16);
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

//    private String geraF(int numF) {
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i < numF; i++) {
//            sb.append("f");
//        }
//        return sb.toString();
//    }
}
