package com.alissonbk.pilacoin.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
})
public class ValidacaoPilaBloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Instant dataAcao;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private Map<String, Object> pilaBlocoJson;

    @NotNull
    @Column(columnDefinition = "text", nullable = false)
    private String nonce;

    @NotNull
    @Column(columnDefinition = "text", nullable = false)
    private String chaveCriador;

    @NotNull
    @Column(nullable = false)
    private TipoPilaBloco tipoPilaBloco;

    @NotNull
    @Column(nullable = false)
    private boolean isOutroUsuario;
}
