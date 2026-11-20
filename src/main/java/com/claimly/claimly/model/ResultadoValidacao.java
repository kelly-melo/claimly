package com.claimly.claimly.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resultados_validacao")
@Getter
@Setter
@NoArgsConstructor
public class ResultadoValidacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;

    @Enumerated(EnumType.STRING)
    private StatusValidacao status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "motivos_validacao",
            joinColumns = @JoinColumn(name = "resultado_id"))
    @Column(name = "motivo")
    private List<String> motivos = new ArrayList<>();

    private String numeroNota;
    private LocalDate dataEmissao;
    private BigDecimal valorTotal;
    private String nomePaciente;
    private String cpfPaciente;
    private String cnpjPrestador;

    private LocalDateTime criadoEm = LocalDateTime.now();
}
