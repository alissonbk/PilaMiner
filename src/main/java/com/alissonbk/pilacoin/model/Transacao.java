package com.alissonbk.pilacoin.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
@Data
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
})
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant dataAcao;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private PilaCoin pilaCoinBlocoJson;

    @Column(columnDefinition = "text")
    private String nonce;

    @Column(columnDefinition = "text")
    private String chaveCriador;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPilaBloco tipoPilaBloco;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipoTransacao;
}
