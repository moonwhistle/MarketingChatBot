package com.example.marketingChatBot.chat.service;

import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.batch.api.ObjectsBatcher;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument;
import io.weaviate.client.v1.graphql.query.builder.GetBuilder;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.graphql.query.fields.Fields;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeaviateService {

    private final WeaviateClient weaviateClient;

    public void save(String question, String answer, String relatedData) {
        ObjectsBatcher batcher = weaviateClient.batch().objectsBatcher();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("category", "Question and Answer");
        properties.put("description", "Saved QnA pair");
        properties.put("metadata", relatedData);
        properties.put("content", question + " " + answer);

        batcher.withObject(WeaviateObject.builder()
                .className("BusinessAPI")
                .properties(properties)
                .build());

        batcher.run();
    }

    public String searchNearTest(String request) {
        NearTextArgument nearText = NearTextArgument.builder()
                .concepts(new String[]{request})
                .build();

        Fields fields = Fields.builder()
                .fields(new Field[]{
                        Field.builder().name("name").build(),
                        Field.builder().name("category").build(),
                        Field.builder().name("description").build(),
                        Field.builder().name("content").build()
                })
                .build();

        String query = GetBuilder.builder()
                .className("BusinessAPI")
                .fields(fields)
                .withNearTextFilter(nearText)
                .limit(10)
                .build()
                .buildQuery();

        Result<GraphQLResponse> result = weaviateClient.graphQL()
                .raw()
                .withQuery(query)
                .run();

        return result.getResult().toString();
    }
}
