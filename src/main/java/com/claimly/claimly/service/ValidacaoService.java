package com.claimly.claimly.service;

import com.claimly.claimly.ia.ExtratorDadosNota;
import com.claimly.claimly.imagem.ConversorDocumento;
import com.claimly.claimly.imagem.TratadorImagem;
import com.claimly.claimly.model.DadosNotaFiscal;
import com.claimly.claimly.model.ResultadoValidacao;
import com.claimly.claimly.model.StatusValidacao;
import com.claimly.claimly.ocr.OcrService;
import com.claimly.claimly.repository.ResultadoValidacaoRepository;
import com.claimly.claimly.validacao.ValidadorNotaFiscal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ValidacaoService {

    private static final Set<String> TIPOS_ACEITOS =
            Set.of("image/jpeg", "image/png", "application/pdf");
    private static final int MINIMO_CARACTERES = 30;

    private final ResultadoValidacaoRepository repository;
    private final ConversorDocumento conversor;
    private final TratadorImagem tratador;
    private final OcrService ocr;
    private final ExtratorDadosNota extrator;
    private final ValidadorNotaFiscal validador;

    public ResultadoValidacao validar(MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("Nenhum arquivo enviado.");
        }
        if (!TIPOS_ACEITOS.contains(arquivo.getContentType())) {
            throw new IllegalArgumentException(
                    "Formato não suportado. Envie JPG, PNG ou PDF.");
        }

        ResultadoValidacao resultado = new ResultadoValidacao();
        resultado.setNomeArquivo(arquivo.getOriginalFilename());

        BufferedImage imagem = conversor.paraImagem(arquivo);
        String texto = ocr.extrairTexto(tratador.tratar(imagem));

        if (texto == null || texto.strip().length() < MINIMO_CARACTERES) {
            resultado.setStatus(StatusValidacao.REPROVADO);
            resultado.getMotivos().add(
                    "Documento ilegível. Envie uma nova foto, com boa iluminação e sem cortes.");
            return repository.save(resultado);
        }

        DadosNotaFiscal dados = extrator.extrair(texto);
        List<String> motivos = validador.validar(dados);

        preencher(resultado, dados);
        resultado.getMotivos().addAll(motivos);
        resultado.setStatus(motivos.isEmpty()
                ? StatusValidacao.APROVADO
                : StatusValidacao.REPROVADO);

        return repository.save(resultado);
    }

    private void preencher(ResultadoValidacao resultado, DadosNotaFiscal dados) {
        resultado.setNumeroNota(dados.numeroNota());
        resultado.setDataEmissao(dados.dataEmissao());
        resultado.setValorTotal(dados.valorTotal());
        resultado.setNomePaciente(dados.nomePaciente());
        resultado.setCpfPaciente(dados.cpfPaciente());
        resultado.setCnpjPrestador(dados.cnpjPrestador());
    }
}
