package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.ValidaCoinRecieveDTO;
import com.alissonbk.pilacoin.dto.ValidaCoinSendDTO;
import com.alissonbk.pilacoin.http.PilaCoinClientHttp;
import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.model.TipoPilaBloco;
import com.alissonbk.pilacoin.model.ValidacaoPilaBloco;
import com.alissonbk.pilacoin.repository.ValidacaoPilaBlocoRepository;
import com.alissonbk.pilacoin.util.Util;
import com.alissonbk.pilacoin.util.UtilGenerators;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class ValidaCoinService {
    private final ValidacaoPilaBlocoRepository repository;
    private final WebSocketServerService webSocketServerService;

    public ValidaCoinService(ValidacaoPilaBlocoRepository repository, WebSocketServerService webSocketServerService) {
        this.repository = repository;
        this.webSocketServerService = webSocketServerService;
    }

    public String validaCoin(ValidaCoinRecieveDTO validaCoinRecieveDTO) {
        final PilaCoin pilaCoin = validaCoinRecieveDTO.toPilaCoin();
        final String pilaJson = UtilGenerators.generateJSON(pilaCoin);
        final BigInteger numHash = UtilGenerators.generateHashBigInteger(pilaJson);

        if (Util.validateMineracao(numHash)) {
            final var pilaCoinClientHttp = new PilaCoinClientHttp();
            final String jsonToSend = createValidationJson(pilaCoin, pilaJson);
            boolean success = pilaCoinClientHttp.validateOtherUserCoin(jsonToSend);

            if (success) {
                saveValidacaoPila(jsonToSend);
                return "Pila coin de outro usuario validado com sucesso";
            } else {
                return "Falha ao validar pila coin de outro usuario!";
            }
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
            validaCoinSendDTO.setHashPilaBloco(UtilGenerators.generateHash(pilaJson));
            validaCoinSendDTO.setChavePublica(KeyGeneratorService.getPublicKeyString());
            final String json = UtilGenerators.generateJSON(validaCoinSendDTO);
            validaCoinSendDTO.setAssinatura(UtilGenerators.generateSignature(json));
            return UtilGenerators.generateJSON(validaCoinSendDTO);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Transactional
    public void saveValidacaoPila(String json) {
        ValidacaoPilaBloco validacaoPilaBloco = new ValidacaoPilaBloco();
        Map<String, Object> pilaCoindSendDTO = UtilGenerators.generateObjectFromJson(json);
        validacaoPilaBloco.setPilaBlocoJson(pilaCoindSendDTO);
        validacaoPilaBloco.setTipoPilaBloco(TipoPilaBloco.PILA_COIN);
        validacaoPilaBloco.setNonce(pilaCoindSendDTO.get("nonce").toString());
        validacaoPilaBloco.setChaveCriador(pilaCoindSendDTO.get("chavePublica").toString());
        validacaoPilaBloco.setDataAcao(Date.from(Instant.now()));
        validacaoPilaBloco.setOutroUsuario(true);
        this.repository.save(validacaoPilaBloco);
        this.webSocketServerService.notifyValidacaoPila(UtilGenerators.generateJSON(validacaoPilaBloco));
    }

}
