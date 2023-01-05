package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.recieve.ValidaCoinRecieveDTO;
import com.alissonbk.pilacoin.dto.send.ValidaCoinSendDTO;
import com.alissonbk.pilacoin.http.PilaCoinClientHttp;
import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.util.Util;
import com.alissonbk.pilacoin.util.UtilGenerators;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class ValidaCoinService {
    public static String validaCoin(ValidaCoinRecieveDTO validaCoinRecieveDTO) {
        final PilaCoin pilaCoin = validaCoinRecieveDTO.toPilaCoin();
        final String pilaJson = UtilGenerators.generateJSON(pilaCoin);
        final BigInteger numHash = UtilGenerators.generateHash(pilaJson);

        if (Util.validateMineracao(numHash)) {
            final var pilaCoinClientHttp = new PilaCoinClientHttp();
            boolean success = pilaCoinClientHttp.validateOtherUserCoin(
                    createValidationJson(pilaCoin, pilaJson)
            );
            return success ? "Pila coin de outro usuario validado com sucesso"
                    : "Falha ao validar pila coin de outro usuario!";
        } else {
            return "PilaCoin não é valido!";
        }
    }


    @SneakyThrows
    private static String createValidationJson(PilaCoin pilaCoin, String pilaJson) {
        var validaCoinSendDTO = new ValidaCoinSendDTO();
        try {
            validaCoinSendDTO.setTipo("PILA");
            validaCoinSendDTO.setNonce(pilaCoin.getNonce());
            validaCoinSendDTO.setHashPilaBloco(UtilGenerators.generateHash(pilaJson).toString());
            validaCoinSendDTO.setChavePublica(KeyGeneratorService.getPublicKeyString());
            validaCoinSendDTO.setAssinatura(generateSignature(validaCoinSendDTO));
            final String json = UtilGenerators.generateJSON(validaCoinSendDTO);
            System.out.println(json);
            return json;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "";
        }
    }

    @SneakyThrows
    private static String generateSignature(ValidaCoinSendDTO validaCoinSendDTO) {
        String json = UtilGenerators.generateJSON(validaCoinSendDTO);
        Cipher cipher = Cipher.getInstance("RSA");
        PublicKey publicKey =
                KeyFactory.getInstance("RSA")
                        .generatePublic(new X509EncodedKeySpec(KeyGeneratorService.getPublicKeyBytes()));
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] hash = UtilGenerators.generateHash(json).toByteArray();
        return Base64.getEncoder().encodeToString(cipher.doFinal(hash));
    }

}
