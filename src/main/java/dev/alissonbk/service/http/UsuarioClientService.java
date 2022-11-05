package dev.alissonbk.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alissonbk.model.Usuario;
import dev.alissonbk.util.ServerEndpoints;
import lombok.SneakyThrows;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
public class UsuarioClientService {

    @SneakyThrows
    public Boolean createUser(byte[] chavePublicaBytes) {

        // verifica se o usuario ja está cadastrado
        Boolean cadastrado = this.usuarioJaExiste(chavePublicaBytes);


        //se não contém cadastra
        if (!cadastrado) {
            ResponseEntity<Usuario> response = null;
            RestTemplate restTemplate = new RestTemplate();
            Usuario newUser = new Usuario();

            newUser.setNome(Usuario.NOME);
            newUser.setChavePublica(Base64.getEncoder().encodeToString(chavePublicaBytes));
            Map<String, Object> mapUser = new HashMap<>();
            mapUser.put("id", 0);
            mapUser.put("nome", newUser.getNome());
            mapUser.put("chavePublica", newUser.getChavePublica());

            try {
                String json = new ObjectMapper().writeValueAsString(mapUser);
                RequestEntity<String> requestEntity = RequestEntity.post(new URL(ServerEndpoints.SAVE_USER + "/").toURI())
                        .contentType(MediaType.APPLICATION_JSON).body(json);
                response = restTemplate.exchange(requestEntity, Usuario.class);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    System.out.println("Bad Request, Usuario provavelmente já existe");
                    e.printStackTrace();
                    return true;
                }
                System.out.println("Falha ao criar usuario! Status code: " + e.getStatusCode());
                e.printStackTrace();
            }

            if (response != null && response.getBody() != null) {
                System.out.println(response.getBody());
                if(response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == HttpStatus.CREATED) {
                    if (this.usuarioJaExiste(chavePublicaBytes)) {
                        System.out.println("Usuario Cadastrado com sucesso!");
                        return true;
                    }
                }
            }
        } else return true; // já esta cadastrado

        return false;
    }

//    private String getUsers() {
//        RestTemplate restTemplate = new RestTemplate();;
//
//        ResponseEntity<String> response
//                = restTemplate.getForEntity(ServerConnect.SERVER_URL + "/usuario/all", String.class);
//        System.out.println("getUsers Status Code: " + response.getStatusCode());
//        System.out.println("getUSers body: " + response.getBody());
//        return response.getBody();
//    }

    @SneakyThrows
    private Boolean usuarioJaExiste(byte[] chavePublicaBytes) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Usuario> usuario = null;

        try {
            RequestEntity<String> requestEntity = RequestEntity.post(new URL(ServerEndpoints.FIND_USER_BY_KEY).toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(Base64.getEncoder().encodeToString(chavePublicaBytes));
            usuario = restTemplate.exchange(requestEntity, Usuario.class);
        } catch (HttpClientErrorException e) {
            return false;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if ( usuario != null && usuario.getBody() != null && usuario.getBody().getChavePublica()
                .equals(Base64.getEncoder().encodeToString(chavePublicaBytes))) {
            System.out.println("Usuario já está cadastrado com esta chave publica!");
            return true;
        }

        return false;
    }
}
