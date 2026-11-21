package com.claimly.claimly.controller;

import com.claimly.claimly.dto.ResultadoValidacaoResponse;
import com.claimly.claimly.service.ValidacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/reembolsos")
@RequiredArgsConstructor
public class ReembolsoController {

    private final ValidacaoService service;

    @PostMapping(value = "/validar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultadoValidacaoResponse> validar(
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(
                ResultadoValidacaoResponse.de(service.validar(arquivo)));
    }
}
