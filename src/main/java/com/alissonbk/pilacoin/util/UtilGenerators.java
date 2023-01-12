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
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
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

    public static byte[] generateHashByteArray(String message) {
        byte[] hash = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
        }catch (NoSuchAlgorithmException e) {
            System.out.println("Algoritmo para gerar HASH incorreto!");
            e.printStackTrace();
        }
        if (hash == null) {
            throw new RuntimeException("Falha ao gerar HASH (a hash gerada é null)");
        }

        return hash;
    }

    @SneakyThrows
    public static String generateSignature(ValidaCoinSendDTO validaCoinSendDTO) {
        String json = UtilGenerators.generateJSON(validaCoinSendDTO);
        Cipher cipher = Cipher.getInstance("RSA");
        PublicKey publicKey =
                KeyFactory.getInstance("RSA")
                        .generatePublic(new X509EncodedKeySpec(KeyGeneratorService.getPublicKeyBytes()));
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] hash = UtilGenerators.generateHash(json).toByteArray();
        return Base64.getEncoder().encodeToString(cipher.doFinal(hash));
    }

    @SneakyThrows
    public static Map<String, Object> generateObjectFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Map.class);
    }
}
