package dev.alissonbk.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alissonbk.model.PilaCoin;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

public class UtilGenerators {

    public static String generateJSON(PilaCoin pilaCoin) {
        String json = "";
        try {
            ObjectMapper om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            json = om.writeValueAsString(pilaCoin);
        } catch (JsonProcessingException e) {
            System.out.println("Falha ao gerar JSON do pila coin!");
            e.printStackTrace();
        }

        if (json.equals("")) {
            throw new RuntimeException("JSON ficou como uma String vazia!");
        }
        return json;
    }

    public static BigInteger generateHash(String pilaJson) {
        byte[] hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(pilaJson.getBytes(StandardCharsets.UTF_8));
        }catch (NoSuchAlgorithmException e) {
            System.out.println("Algoritmo para gerar HASH incorreto!");
            e.printStackTrace();
        }
        if (hash == null) {
            throw new RuntimeException("Falha ao gerar HASH (a hash gerada é null)");
        }

        return new BigInteger(hash).abs();
    }
}
