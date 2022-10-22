package dev.alissonbk;

import dev.alissonbk.service.KeyPairGeneratorService;
import dev.alissonbk.service.MineracaoService;

public class Main {
    private static KeyPairGeneratorService keyPairGeneratorService = new KeyPairGeneratorService();
    private static MineracaoService mineracaoService = new MineracaoService();


    public static void main(String[] args) {
        keyPairGeneratorService.generateKeys();
        mineracaoService.miningLoop(mineracaoService.initialize());
    }
}
