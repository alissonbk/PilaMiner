package com.alissonbk.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public static final String NOME = "Alisson Billig Kroth";
    //public int id = null;

    @NotNull
    @Column(nullable = false)
    private String nome;

    @NotNull
    @Column(columnDefinition = "text", nullable = false, unique = true)
    private String chavePublica;
    private byte[] chavePublicaBytes;

    @Column(columnDefinition = "text", nullable = false, unique = true)
    private String chavePrivada;
    private byte[] chavePrivadaBytes;

    @NotNull
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @NotNull
    @Column(nullable = false)
    private boolean isAtivo = true;

    @Transient
    @JsonProperty("access_token")
    private String accessToken;

}
