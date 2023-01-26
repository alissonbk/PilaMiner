package com.alissonbk.pilacoin.util;

import com.alissonbk.pilacoin.configuration.MineracaoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    public static <T> List<T> jsonArrayToList(String json, Class<T> elementClass) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType listType =
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, elementClass);
        return objectMapper.readValue(json, listType);
    }
}
