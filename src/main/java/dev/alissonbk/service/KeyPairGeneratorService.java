package dev.alissonbk.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;


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
                        Files.write(Path.of(PUBLIC_KEY_RELATIVE_PATH), keyPair.getPrivate().getEncoded());
                        System.out.println("Public key: \t" + Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));

                        Files.write(Path.of(PRIVATE_KEY_RELATIVE_PATH), keyPair.getPrivate().getEncoded());
                        System.out.println("Private key: \t" + Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));

                        LOG.info("Os arquivos com a chave publica e privada foram gerados e salvos com sucesso! " +
                                "\n Public: " + PUBLIC_KEY_RELATIVE_PATH + "\n Private: " + PRIVATE_KEY_RELATIVE_PATH);
                    } catch (IOException e) {
                        LOG.warning("Falha ao salvar arquivos com as chaves");
                        e.printStackTrace();
                    }
                }
            }


        }



    }
}
