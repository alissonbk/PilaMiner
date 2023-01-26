package com.alissonbk.pilacoin.util;

import com.alissonbk.pilacoin.configuration.MineracaoConfiguration;

import java.math.BigInteger;

public class Util {

    /**
     * Valida se a hash é menor que a dificuldade ou seja se foi minerado
     * */
    public static boolean validateMineracao(BigInteger hash) {
        if (MineracaoConfiguration.DIFICULDADE == null) {
            System.out.println("Impossível validar mineração antes de receber o valor da dificuldade!");
            return false;
        }
        return hash.compareTo(MineracaoConfiguration.DIFICULDADE) < 0;
    }

}
