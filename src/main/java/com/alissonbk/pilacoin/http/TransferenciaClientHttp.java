package com.alissonbk.pilacoin.http;

import antlr.StringUtils;
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
public class TransferenciaClientHttp {
    @SneakyThrows
    public boolean transferir(String transferenciaJson) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<?> response = null;
        StringBuilder message = null;
        try {
            RequestEntity<String> requestEntity = RequestEntity.post(
                    new URL(ServerEndpoints.TRANSFER_PILA_COIN).toURI()
            ).contentType(MediaType.APPLICATION_JSON).body(transferenciaJson);
            response = restTemplate.exchange(requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusText());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (response == null) return false;
        System.out.println("Response Body Transferencia: " + response.getBody());
        System.out.println("Response Status Transferencia: " + response.getStatusCode());

        return response.getStatusCode().equals(HttpStatus.OK);
    }
}
