package dev.alissonbk.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alissonbk.model.PilaCoin;
import dev.alissonbk.model.Usuario;
import dev.alissonbk.util.ServerEndpoints;
import dev.alissonbk.util.UtilGenerators;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Base64;

@Service
public class PilaCoinClientService {

    @SneakyThrows
    public boolean submitPilaCoin(PilaCoin pilaCoin) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PilaCoin> response = null;
        try {
            String json = UtilGenerators.generateJSON(pilaCoin);
            System.out.println(json);
            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                    ServerEndpoints.PILA_COIN_VALIDATOR + "/").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(json);
            response = restTemplate.exchange(requestEntity, PilaCoin.class);
            System.out.println("submitPilaCoin StatusCode: " + response.getStatusCode());
            System.out.println("submitPilaCoin Response: " + response.getBody());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return true;
        }
        return false;
    }

    @SneakyThrows
    public boolean verifyPilaCoinExists(PilaCoin pilaCoin) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PilaCoin> pilaCoinResponse = null;

        try {
            RequestEntity<String> requestEntity =
                    RequestEntity.post(
                            new URL(ServerEndpoints.PILA_COIN_EXISTS + "/?nonce=" + pilaCoin.getNonce()).toURI())
                            .contentType(MediaType.APPLICATION_JSON).body(new ObjectMapper().writeValueAsString(""));

            pilaCoinResponse = restTemplate.exchange(requestEntity, PilaCoin.class);
        } catch (HttpClientErrorException e) {
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (pilaCoinResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            System.out.println("Nonce não encontrado !");
            return false;
        }
        if (pilaCoinResponse.getBody().getNonce() == pilaCoin.getNonce()) {
            System.out.println("Nonce encontrado!!");
            return true;
        }
        return false;
    }

}
