package com.example.marketingChatBot.chat.service;

import com.example.marketingChatBot.chat.controller.dto.Response.ChatResponse;
import com.example.marketingChatBot.chat.controller.dto.request.ChatRequest;

import com.example.marketingChatBot.chat.service.client.ChatClient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final WeaviateService weaviateService;

    public ChatResponse getAnswer(ChatRequest request) {
        String relatedData = String.valueOf(weaviateService.searchNearTest(request.message()));
        String answer = chatClient.callOpenAiApi(request.message(), relatedData);
        weaviateService.save(request.message(), answer, relatedData);
        return new ChatResponse(answer);
    }
}
