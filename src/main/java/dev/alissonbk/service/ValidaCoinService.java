package dev.alissonbk.service;

import dev.alissonbk.dto.recieve.ValidaCoinRecieveDTO;
import dev.alissonbk.dto.send.ValidaCoinSendDTO;
import dev.alissonbk.http.PilaCoinClientHttp;
import dev.alissonbk.model.Mineracao;
import dev.alissonbk.model.PilaCoin;
import dev.alissonbk.model.Usuario;
import dev.alissonbk.util.Util;
import dev.alissonbk.util.UtilGenerators;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class ValidaCoinService {
    public static String validaCoin(ValidaCoinRecieveDTO validaCoinRecieveDTO) {
        final var pilaCoin = validaCoinRecieveDTO.toPilaCoin();
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
