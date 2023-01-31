package com.alissonbk.pilacoin.http;

import com.alissonbk.pilacoin.model.Bloco;
import com.alissonbk.pilacoin.util.ServerEndpoints;
import com.alissonbk.pilacoin.util.Util;
import lombok.SneakyThrows;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.List;

@Service
public class BlocoClientHttp {

    @SneakyThrows
    public List<Bloco> getAllBlocos() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = null;
        List<Bloco> blocos = null;

        try {
            RequestEntity<Void> requestEntity =
                    RequestEntity.get(new URL(ServerEndpoints.GET_ALL_BLOCKS).toURI()).build();
            response = restTemplate.exchange(requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if ( response != null && response.getBody() != null && !response.getBody().isEmpty() ) {
            blocos = Util.jsonArrayToList(response.getBody(), Bloco.class);
        }

        return blocos;
    }
}
