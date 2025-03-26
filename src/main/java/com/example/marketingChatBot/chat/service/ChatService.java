package com.example.marketingChatBot.chat.service;

import com.example.marketingChatBot.chat.controller.dto.request.ChatRequest;

import com.example.marketingChatBot.chat.service.client.ChatClient;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public ChatService(EmbeddingModel embeddingModel, @Qualifier("vectorDB") VectorStore vectorStore,
                       ChatClient chatClient) {
        this.embeddingModel = embeddingModel;
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public String getAnswer(ChatRequest request) {
        String relatedData = searchRelatedData(request);
        String answer = chatClient.callOpenAiApi(request.message(), relatedData);
        return answer;
    }

    private String searchRelatedData(ChatRequest request) {
        List<Document> relatedData =  vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(request.message())
                        .build()
        );

        assert relatedData != null;
        if(relatedData.isEmpty()) {
            return "관련된 데이터가 없습니다.";
        }

        return relatedData.get(0).getFormattedContent();
    }
}
