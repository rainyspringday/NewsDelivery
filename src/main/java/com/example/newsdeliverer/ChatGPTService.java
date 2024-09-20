package com.example.newsdeliverer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


@Service
public class ChatGPTService {
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";
    private final String apiKey = "testkey";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    public ChatGPTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String chatWithGpt(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("max_tokens", 1000);

        String body;
        try {
            body = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error creating JSON payload", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return extractContentFromResponse(response.getBody());
        } else {
            throw new RuntimeException("Failed to get response from OpenAI API. Status code: " + response.getStatusCode());
        }
    }
    public String analyzeNewsArticles(List<String> articles) {
        StringBuilder promptBuilder = new StringBuilder("Identify for each article if it is local or global and the city it belongs to, return result as a list where values of each article separated by space(example: local New York global Dallas) and nothing else\n");
        for (String article : articles) {
            promptBuilder.append(article).append("\n");
        }
        return chatWithGpt(promptBuilder.toString());
    }

    private String extractContentFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = rootNode.path("choices");

            // Assuming there is at least one choice
            if (choicesNode.isArray() && !choicesNode.isEmpty()) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                return messageNode.path("content").asText();
            } else {
                throw new RuntimeException("No choices found in the response.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error parsing response JSON", e);
        }
    }
}
