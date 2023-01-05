package com.alissonbk.pilacoin.service;


import com.alissonbk.pilacoin.model.Mineracao;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.Base64;


@Service
public class KeyGeneratorService {

    public static final String PUBLIC_KEY_RELATIVE_PATH = "src/main/resources/keyfiles/publicKey.txt";
    public static final String PRIVATE_KEY_RELATIVE_PATH = "src/main/resources/keyfiles/privateKey.txt";
    public static final String MASTER_PUBLIC_KEY_RELATIVE_PATH = "src/main/resources/keyfiles/master-pub.key";

    public void generateKeys() {

        if (new File(PUBLIC_KEY_RELATIVE_PATH).exists() && new File(PRIVATE_KEY_RELATIVE_PATH).exists()) {
            System.out.println("Os arquivos com a chave publica e privada já estão criados em ex: " + PUBLIC_KEY_RELATIVE_PATH);
        } else {
            KeyPair keyPair = null;
            try {
                KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                keyPair = keyPairGenerator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Falha ao gerar par de chaves!!!");
                e.printStackTrace();
            } finally {
                if (keyPair == null) {
                    System.out.println("KeyPair é nulo!!");
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
                        System.out.println("Falha ao salvar arquivos com as chaves");
                        e.printStackTrace();
                    }
                }
            }


        }

    }

    @SneakyThrows
    public Mineracao generateMineracaoWithKeys() {
        byte[] publicKeyBytes = Files.readAllBytes(Path.of(KeyGeneratorService.PUBLIC_KEY_RELATIVE_PATH));
        System.out.println(("Chave publica Base64: " + Base64.getEncoder().encodeToString(publicKeyBytes)));


        byte[] privateKeyBytes = Files.readAllBytes(Path.of(KeyGeneratorService.PRIVATE_KEY_RELATIVE_PATH));
        System.out.println(("Chave privada Base64: " + Base64.getEncoder().encodeToString(privateKeyBytes)));

        // keypair
//        PublicKey publicKey = KeyFactory.getInstance("RSA")
//                .generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        //System.out.println(publicKey);

//        PrivateKey privateKey = KeyFactory.getInstance("RSA")
//                .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        //System.out.println(privateKey);

        //KeyPair keyPair = new KeyPair(publicKey, privateKey);


        return new Mineracao(publicKeyBytes, privateKeyBytes);
    }

    @SneakyThrows
    public static String getPublicKeyString() {
        return Base64.getEncoder()
                .encodeToString(Files.readAllBytes(Path.of(KeyGeneratorService.PUBLIC_KEY_RELATIVE_PATH)));
    }

    @SneakyThrows
    public static byte[] getPublicKeyBytes() {
        return Files.readAllBytes(Path.of(KeyGeneratorService.PUBLIC_KEY_RELATIVE_PATH));
    }

    @SneakyThrows
    public static String getPrivateKeyString() {
        return Base64.getEncoder()
                .encodeToString(Files.readAllBytes(Path.of(KeyGeneratorService.PRIVATE_KEY_RELATIVE_PATH)));
    }

    @SneakyThrows
    public static byte[] getPrivateKeyBytes() {
        return Files.readAllBytes(Path.of(KeyGeneratorService.PRIVATE_KEY_RELATIVE_PATH));
    }

    @SneakyThrows
    public String getMasterPublicKey() {
        byte[] masterPubBytes = Files.readAllBytes(Path.of(KeyGeneratorService.MASTER_PUBLIC_KEY_RELATIVE_PATH));
        return Base64.getEncoder().encodeToString(masterPubBytes);
    }
}
