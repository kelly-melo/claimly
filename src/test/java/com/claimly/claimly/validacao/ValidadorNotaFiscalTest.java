package com.claimly.claimly.validacao;

import com.claimly.claimly.model.DadosNotaFiscal;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ValidadorNotaFiscalTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 10, 1);
    private static final String CPF_VALIDO = "529.982.247-25";
    private static final String CNPJ_VALIDO = "11.222.333/0001-81";

    private final ValidadorNotaFiscal validador = new ValidadorNotaFiscal();

    private DadosNotaFiscal nota(LocalDate data, String cpf, String cnpj) {
        return new DadosNotaFiscal(
                "12345", data, new BigDecimal("250.00"), "Consulta cardiologia",
                "João da Silva", cpf, "Clínica Vida", cnpj,
                "10101012", "Consulta em consultório", "0001234");
    }

    @Test
    void notaValidaNaoGeraMotivos() {
        var dados = nota(HOJE.minusDays(10), CPF_VALIDO, CNPJ_VALIDO);

        assertThat(validador.validar(dados, HOJE)).isEmpty();
    }

    @Test
    void notaComExatamente90DiasEhAceita() {
        var dados = nota(HOJE.minusDays(90), CPF_VALIDO, CNPJ_VALIDO);

        assertThat(validador.validar(dados, HOJE)).isEmpty();
    }

    @Test
    void notaComMaisDe90DiasEhRejeitada() {
        var dados = nota(HOJE.minusDays(91), CPF_VALIDO, CNPJ_VALIDO);

        assertThat(validador.validar(dados, HOJE))
                .containsExactly("Nota emitida há mais de 90 dias.");
    }

    @Test
    void cpfAusenteGeraMotivoDeCampoObrigatorio() {
        var dados = nota(HOJE.minusDays(5), null, CNPJ_VALIDO);

        assertThat(validador.validar(dados, HOJE))
                .containsExactly("Campo obrigatório não encontrado: CPF do paciente.");
    }

    @Test
    void cpfInvalidoEhRejeitado() {
        var dados = nota(HOJE.minusDays(5), "111.111.111-11", CNPJ_VALIDO);

        assertThat(validador.validar(dados, HOJE))
                .containsExactly("CPF do paciente inválido.");
    }

    @Test
    void cnpjInvalidoEhRejeitado() {
        var dados = nota(HOJE.minusDays(5), CPF_VALIDO, "11.222.333/0001-82");

        assertThat(validador.validar(dados, HOJE))
                .containsExactly("CNPJ do prestador inválido.");
    }
}
