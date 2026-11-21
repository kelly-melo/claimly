package com.claimly.claimly.service;

import com.claimly.claimly.model.ResultadoValidacao;
import com.claimly.claimly.model.StatusValidacao;
import com.claimly.claimly.repository.ResultadoValidacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ValidacaoService {

    private static final Set<String> TIPOS_ACEITOS =
            Set.of("image/jpeg", "image/png", "application/pdf");

    private final ResultadoValidacaoRepository repository;

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
        resultado.setStatus(StatusValidacao.REVISAO_MANUAL);
        resultado.getMotivos().add("Leitura do documento ainda não implementada.");

        return repository.save(resultado);
    }
}
