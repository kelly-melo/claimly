package com.claimly.claimly.ia;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {

    private final RestClient restClient;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String modelo;

    public GeminiClient(ObjectMapper mapper,
                        @Value("${gemini.api-key:}") String apiKey,
                        @Value("${gemini.modelo}") String modelo,
                        @Value("${gemini.url-base}") String urlBase) {
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.modelo = modelo;
        this.restClient = RestClient.builder().baseUrl(urlBase).build();
    }

    public String gerarJson(String prompt) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Chave da API Gemini não configurada (variável GEMINI_API_KEY).");
        }

        Map<String, Object> corpo = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0,
                        "responseMimeType", "application/json"));

        try {
            String resposta = restClient.post()
                    .uri("/models/{modelo}:generateContent", modelo)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(corpo))
                    .retrieve()
                    .body(String.class);

            Resposta r = mapper.readValue(resposta, Resposta.class);
            return r.candidates().get(0).content().parts().get(0).text();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao consultar a API Gemini.", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resposta(List<Candidato> candidates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidato(Conteudo content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Conteudo(List<Parte> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parte(String text) {
    }
}
