package dev.alissonbk.util;

import dev.alissonbk.model.Mineracao;

import java.math.BigInteger;

public class Util {

    /**
     * Valida se a hash é menor que a dificuldade ou seja se foi minerado
     * */
    public static boolean validateMineracao(BigInteger hash) {
        return hash.compareTo(Mineracao.DIFICULDADE) < 0;
    }

}
