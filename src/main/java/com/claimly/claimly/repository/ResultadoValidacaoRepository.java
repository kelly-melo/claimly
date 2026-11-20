package com.claimly.claimly.repository;

import com.claimly.claimly.model.ResultadoValidacao;
import com.claimly.claimly.model.StatusValidacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultadoValidacaoRepository extends JpaRepository<ResultadoValidacao, Long> {

    List<ResultadoValidacao> findByStatus(StatusValidacao status);
}
