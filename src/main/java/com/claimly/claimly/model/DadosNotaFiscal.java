package com.claimly.claimly.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DadosNotaFiscal(
        String numeroNota,
        LocalDate dataEmissao,
        BigDecimal valorTotal,
        String descricaoServico,
        String nomePaciente,
        String cpfPaciente,
        String razaoSocialPrestador,
        String cnpjPrestador,
        String codigoProcedimento,
        String descricaoProcedimento,
        String numeroCarteirinha
) {
}
