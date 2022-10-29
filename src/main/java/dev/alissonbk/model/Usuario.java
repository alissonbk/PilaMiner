package dev.alissonbk.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    public static final String NOME = "usuario teste";
    //public int id = null;
    private String nome;
    private String chavePublica;
    private byte[] chavePublicaBytes;
}
