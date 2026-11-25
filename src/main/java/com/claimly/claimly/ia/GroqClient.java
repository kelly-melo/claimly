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
public class GroqClient {

    private final RestClient restClient;
    private final ObjectMapper mapper;
    private final String apiKey;
    private final String modelo;

    public GroqClient(ObjectMapper mapper,
                      @Value("${groq.api-key:}") String apiKey,
                      @Value("${groq.modelo}") String modelo,
                      @Value("${groq.url-base}") String urlBase) {
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.modelo = modelo;
        this.restClient = RestClient.builder().baseUrl(urlBase).build();
    }

    public String gerarJson(String prompt) {
        if (apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Chave da API Groq não configurada (variável GROQ_API_KEY).");
        }

        Map<String, Object> corpo = Map.of(
                "model", modelo,
                "temperature", 0,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(Map.of("role", "user", "content", prompt)));

        try {
            String resposta = restClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(corpo))
                    .retrieve()
                    .body(String.class);

            Resposta r = mapper.readValue(resposta, Resposta.class);
            return r.choices().get(0).message().content();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao consultar a API Groq.", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resposta(List<Escolha> choices) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Escolha(Mensagem message) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Mensagem(String content) {
    }
}
