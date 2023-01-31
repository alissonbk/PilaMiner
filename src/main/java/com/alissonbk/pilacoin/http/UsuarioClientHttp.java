package com.alissonbk.pilacoin.http;

import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.service.UsuarioService;
import com.alissonbk.pilacoin.util.ServerEndpoints;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class UsuarioClientHttp {

    private final UsuarioService usuarioService;

    public UsuarioClientHttp(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @SneakyThrows
    public Usuario createUser(Usuario newUser) {

        // verifica se o usuario ja está cadastrado
        Boolean cadastrado = this.getUsuarioByChave(newUser.getChavePublica()).getChavePublica() != null;


        //se não contém o usuario, cadastra
        if (!cadastrado) {
            ResponseEntity<Usuario> response = null;
            RestTemplate restTemplate = new RestTemplate();



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
                    return null;
                }
                System.out.println("Falha ao criar usuario! Status code: " + e.getStatusCode());
                e.printStackTrace();
            }

            if (response != null && response.getBody() != null) {
                System.out.println(response.getBody());
                if(response.getStatusCode() == HttpStatus.ACCEPTED || response.getStatusCode() == HttpStatus.CREATED) {
                    if (this.getUsuarioByChave(newUser.getChavePublica()).getChavePublica() != null) {
                        System.out.println("Usuario Cadastrado com sucesso!");
                        return newUser;
                    }
                }
            }
        } else return newUser; // já esta cadastrado

        return null;
    }

    @SneakyThrows
    public Usuario getUsuarioByChave(String chavePublica) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Usuario> usuario = null;

        try {
            RequestEntity<String> requestEntity = RequestEntity.post(new URL(ServerEndpoints.FIND_USER_BY_KEY).toURI())
                    .contentType(MediaType.APPLICATION_JSON).body(chavePublica);
            usuario = restTemplate.exchange(requestEntity, Usuario.class);
        } catch (HttpClientErrorException e) {
            return new Usuario();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        if ( usuario != null && usuario.getBody() != null) {
            var u = new Usuario();
            u.setNome(usuario.getBody().getNome());
            u.setChavePublica(usuario.getBody().getChavePublica());
            return u;
        }

        return new Usuario();
    }
}
