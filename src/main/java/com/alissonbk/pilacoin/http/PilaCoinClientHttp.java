package com.alissonbk.pilacoin.http;

import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.util.ServerEndpoints;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

@Service
public class PilaCoinClientHttp {

    @SneakyThrows
    public boolean submitPilaCoin(String pilaCoinJson) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PilaCoin> response = null;
        try {
            RequestEntity<String> requestEntity = RequestEntity.post(new URL(
                    ServerEndpoints.PILA_COIN_VALIDATOR + "/").toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(pilaCoinJson);
            response = restTemplate.exchange(requestEntity, PilaCoin.class);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return response.getStatusCode().equals(HttpStatus.OK);
    }

    public boolean verifyPilaCoinExists(PilaCoin pilaCoin) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> pilaCoinResponse = null;

        try {
            final String URL = ServerEndpoints.PILA_COIN_EXISTS + "/?nonce=" + pilaCoin.getNonce();
            pilaCoinResponse = restTemplate.getForEntity(URL, String.class);
        } catch (HttpClientErrorException e) {
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (pilaCoinResponse == null) {
            System.out.println("Response pila coin is null");
            return false;
        }
        if (pilaCoinResponse.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            System.out.println("Nonce não encontrado !");
            return false;
        }

        return pilaCoinResponse.getStatusCode().equals(HttpStatus.OK);
    }

    @SneakyThrows
    public boolean validateOtherUserCoin(String validaCoinSendJson) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<?> response = null;
        try {
            RequestEntity<String> requestEntity = RequestEntity.post(
                    new URL(ServerEndpoints.VALIDATE_COIN_OTHER_USER).toURI()
            ).contentType(MediaType.APPLICATION_JSON).body(validaCoinSendJson);
            response = restTemplate.exchange(requestEntity, String.class);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if (response == null) return false;
        return response.getStatusCode().equals(HttpStatus.OK);
    }

}
