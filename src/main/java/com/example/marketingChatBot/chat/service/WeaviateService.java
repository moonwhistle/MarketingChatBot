package com.example.marketingChatBot.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.graphql.GraphQL;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.Get;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WeaviateService {

    private static final String SAVE_URL = "http://localhost:8081/v1/objects";

    @Value("${spring.ai.openai.api-key}")
    private String OPENAI_API_KEY;

    private final RestTemplate restTemplate;
    private final EmbeddingModel embeddingModel;
    private final WeaviateClient weaviateClient;

    public void saveQnA(String question, String answer, String relatedData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();

        properties.put("name", "QnA");
        properties.put("category", "Question and Answer");
        properties.put("description", "Saved QnA pair");
        properties.put("metadata", relatedData);
        properties.put("content", question + " " + answer);

        body.put("class", "BusinessAPI");
        body.put("properties", properties);

        String jsonBody = objectMapper.writeValueAsString(body);

        HttpHeaders headers = setHeaders();
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        restTemplate.exchange(SAVE_URL, HttpMethod.POST, entity, String.class);
    }

    private HttpHeaders setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Openai-Api-Key", OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Optional<String> searchWithGraph(String queryText) throws JsonProcessingException {
        GraphQL graphQL = weaviateClient.graphQL();

        Get getQuery = graphQL.get()
                .withClassName("BusinessAPI")
                .withFields(
                        Field.builder().name("name").build(),
                        Field.builder().name("category").build(),
                        Field.builder().name("description").build(),
                        Field.builder().name("content").build()
                )
                .withNearVector(
                        NearVectorArgument.builder()
                                .vector(embedRequest(queryText))
                                .build()
                )
                .withLimit(10);

        Result<GraphQLResponse> graphQLResponse = getQuery.run();

        if (graphQLResponse.getResult().getData() == null) {
            return Optional.empty();
        }

        String response = new ObjectMapper().writeValueAsString(graphQLResponse.getResult().getData());
        return response.describeConstable();
    }

    private Float[] embedRequest(String request) {
        float[] embeddings = embeddingModel.embed(request);

        Float[] floatArray = new Float[embeddings.length];
        for (int i = 0; i < embeddings.length; i++) {
            floatArray[i] = embeddings[i];
        }

        return floatArray;
    }
}
