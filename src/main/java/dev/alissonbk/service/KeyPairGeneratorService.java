package dev.alissonbk.service;


import dev.alissonbk.model.Mineracao;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class KeyPairGeneratorService {

    public static final String PUBLIC_KEY_RELATIVE_PATH = "src/main/resources/keyfiles/publicKey.txt";
    public static final String PRIVATE_KEY_RELATIVE_PATH = "src/main/resources/keyfiles/privateKey.txt";
    public static final Logger LOG = Logger.getLogger(KeyPairGeneratorService.class.getName());

    public void generateKeys() {

        if (new File(PUBLIC_KEY_RELATIVE_PATH).exists() && new File(PRIVATE_KEY_RELATIVE_PATH).exists()) {
            LOG.info("Os arquivos com a chave publica e privada já estão criados em ex: " + PUBLIC_KEY_RELATIVE_PATH);
        } else {
            KeyPair keyPair = null;
            try {
                KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                keyPair = keyPairGenerator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                LOG.warning("Falha ao gerar par de chaves!!!");
                e.printStackTrace();
            } finally {
                if (keyPair == null) {
                    LOG.warning("KeyPair é nulo!!");
                    throw new RuntimeException("KeyPair é nulo!!");
                } else {
                    try {
                        Files.write(Path.of(PUBLIC_KEY_RELATIVE_PATH), keyPair.getPublic().getEncoded());
                        System.out.println("Public key: \t" + Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));

                        Files.write(Path.of(PRIVATE_KEY_RELATIVE_PATH), keyPair.getPrivate().getEncoded());
                        System.out.println("Private key: \t" + Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

                        System.out.println(("Os arquivos com a chave publica e privada foram gerados e salvos com sucesso! " +
                                "\n Public: " + PUBLIC_KEY_RELATIVE_PATH + "\n Private: " + PRIVATE_KEY_RELATIVE_PATH));
                    } catch (IOException e) {
                        LOG.warning("Falha ao salvar arquivos com as chaves");
                        e.printStackTrace();
                    }
                }
            }


        }

    }

    @SneakyThrows
    public Mineracao getKeysFromFile() {
        byte[] publicKeyBytes = Files.readAllBytes(Path.of(KeyPairGeneratorService.PUBLIC_KEY_RELATIVE_PATH));
        System.out.println(("Chave publica Base64: " + Base64.getEncoder().encodeToString(publicKeyBytes)));


        byte[] privateKeyBytes = Files.readAllBytes(Path.of(KeyPairGeneratorService.PRIVATE_KEY_RELATIVE_PATH));
        System.out.println(("Chave privada Base64: " + Base64.getEncoder().encodeToString(privateKeyBytes)));

        PublicKey publicKey = KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        //System.out.println(publicKey);

        PrivateKey privateKey = KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        //System.out.println(privateKey);

        KeyPair keyPair = new KeyPair(publicKey, privateKey);


        return new Mineracao(publicKeyBytes, privateKeyBytes);
    }
}
