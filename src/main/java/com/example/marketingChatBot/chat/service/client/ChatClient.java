package com.example.marketingChatBot.chat.service.client;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ChatClient {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;

    @Value("${spring.ai.openai.api-key}")
    private String OPENAI_API_KEY;

    public ChatClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callOpenAiApi(String request, String relatedData) {
        HttpHeaders headers = setHeaders();
        JSONObject body = setBody(request, relatedData);

        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                API_URL,
                HttpMethod.POST,
                entity,
                String.class);

        return getAnswer(responseEntity.getBody());
    }

    private String getAnswer(String response) {
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray choices = jsonResponse.getJSONArray("choices");
        JSONObject firstChoice = choices.getJSONObject(0);

        JSONObject message = firstChoice.getJSONObject("message");
        return message.getString("content");
    }

    private HttpHeaders setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        return headers;
    }

    private JSONObject setBody(String request, String relatedData) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", "관련 문서: " + relatedData));
        messages.put(new JSONObject().put("role", "user").put("content", request));
        requestBody.put("messages", messages);

        return requestBody;
    }

}
