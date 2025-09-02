package com.jasonlat.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.vector-table-name}")
    private String vectorTableName;

    @Value("${spring.ai.ollama.embedding-model}")
    private String optionsModel;


    @Bean
    public OllamaApi ollamaApi(@Value("${spring.ai.ollama.base-url}") String baseUrl) {
        return new OllamaApi.Builder().baseUrl(baseUrl).build();
    }

    @Bean
    public OllamaChatModel ollamaChatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model(chatModel).build())
                .build();
    }

    @Bean("ollamaSimpleVectorStore")
    public SimpleVectorStore vectorStore(OllamaApi ollamaApi) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel
                .builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model(optionsModel).build())
                .build();
        return SimpleVectorStore.builder(embeddingModel).build();
    }


    @Bean("ollamaPgVectorStore")
    public PgVectorStore pgVectorStore(OllamaApi ollamaApi, JdbcTemplate jdbcTemplate) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel
                .builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder().model(optionsModel).build())
                .build();
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName(vectorTableName)
                .build();
    }


    @Value("${spring.ai.ollama.chat.options.model}")
    private String chatModel;

    @Bean(name = "ollamaChatClient")
    public ChatClient chatClient(OllamaChatModel ollamaChatModel, @Qualifier("syncMcpToolCallbackProvider")SyncMcpToolCallbackProvider tools, ChatMemory chatMemory) {
        DefaultChatClientBuilder defaultChatClientBuilder = new DefaultChatClientBuilder(ollamaChatModel, ObservationRegistry.NOOP, null);
        return defaultChatClientBuilder
                .defaultToolCallbacks(tools)
                .defaultOptions(OllamaOptions.builder()
                        .model(chatModel)
                        .build())
                .build();
    }


}
