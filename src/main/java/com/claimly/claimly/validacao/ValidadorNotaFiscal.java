package com.claimly.claimly.validacao;

import com.claimly.claimly.model.DadosNotaFiscal;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class ValidadorNotaFiscal {

    private static final long PRAZO_MAXIMO_DIAS = 90;

    public List<String> validar(DadosNotaFiscal dados) {
        return validar(dados, LocalDate.now());
    }

    public List<String> validar(DadosNotaFiscal dados, LocalDate hoje) {
        List<String> motivos = new ArrayList<>();

        exigir(dados.numeroNota(), "número da nota", motivos);
        exigir(dados.dataEmissao(), "data de emissão", motivos);
        exigir(dados.valorTotal(), "valor total", motivos);
        exigir(dados.cpfPaciente(), "CPF do paciente", motivos);
        exigir(dados.cnpjPrestador(), "CNPJ do prestador", motivos);
        exigir(dados.descricaoProcedimento(), "procedimento", motivos);

        if (dados.cpfPaciente() != null && !ValidadorCpf.valido(dados.cpfPaciente())) {
            motivos.add("CPF do paciente inválido.");
        }
        if (dados.cnpjPrestador() != null && !ValidadorCnpj.valido(dados.cnpjPrestador())) {
            motivos.add("CNPJ do prestador inválido.");
        }
        if (dados.valorTotal() != null && dados.valorTotal().signum() <= 0) {
            motivos.add("Valor total deve ser maior que zero.");
        }
        if (dados.dataEmissao() != null) {
            if (dados.dataEmissao().isAfter(hoje)) {
                motivos.add("Data de emissão está no futuro.");
            } else if (ChronoUnit.DAYS.between(dados.dataEmissao(), hoje) > PRAZO_MAXIMO_DIAS) {
                motivos.add("Nota emitida há mais de 90 dias.");
            }
        }
        return motivos;
    }

    private void exigir(Object valor, String campo, List<String> motivos) {
        boolean ausente = valor == null
                || (valor instanceof String s && s.isBlank());
        if (ausente) {
            motivos.add("Campo obrigatório não encontrado: " + campo + ".");
        }
    }
}
