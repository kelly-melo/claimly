package com.claimly.claimly.dto;

import com.claimly.claimly.model.ResultadoValidacao;
import com.claimly.claimly.model.StatusValidacao;

import java.time.LocalDateTime;
import java.util.List;

public record ResultadoValidacaoResponse(
        Long id,
        StatusValidacao status,
        List<String> motivos,
        LocalDateTime criadoEm
) {
    public static ResultadoValidacaoResponse de(ResultadoValidacao r) {
        return new ResultadoValidacaoResponse(
                r.getId(), r.getStatus(), r.getMotivos(), r.getCriadoEm());
    }
}
