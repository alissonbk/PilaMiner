package com.alissonbk.pilacoin.util;

import com.alissonbk.pilacoin.dto.ValidaCoinSendDTO;
import com.alissonbk.pilacoin.service.KeyGeneratorService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtilGenerators {

    public static String generateJSON(Object o) {
        String json = "";
        try {
            ObjectMapper om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            json = om.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            System.out.println("Falha ao gerar JSON do objeto: " + Object.class);
            e.printStackTrace();
        }

        if (json.equals("")) {
            throw new RuntimeException("JSON ficou como uma String vazia!");
        }
        return json;
    }

    public static BigInteger generateHashBigInteger(String msg) {
        byte[] hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(msg.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algoritmo para gerar HASH incorreto!");
            e.printStackTrace();
        }
        if (hash == null) {
            throw new RuntimeException("Falha ao gerar HASH (a hash gerada é null)");
        }

        return new BigInteger(hash).abs();
    }

    public static byte[] generateHash(String msg) {
        byte[] hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(msg.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Algoritmo para gerar HASH incorreto!");
            e.printStackTrace();
        }
        if (hash == null) {
            throw new RuntimeException("Falha ao gerar HASH (a hash gerada é null)");
        }

        return hash;
    }

    @SneakyThrows
    public static byte[] generateSignature(String json) {
        var rsa = Cipher.getInstance("RSA");
        PrivateKey privateKey =
                KeyFactory.getInstance("RSA")
                        .generatePrivate(new PKCS8EncodedKeySpec(KeyGeneratorService.getPrivateKeyBytes()));

        rsa.init(Cipher.ENCRYPT_MODE, privateKey);

        return rsa.doFinal(UtilGenerators.generateHash(json));
    }

    @SneakyThrows
    public static Map<String, Object> generateObjectFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, Map.class);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
