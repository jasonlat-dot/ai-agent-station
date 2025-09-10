package com.jasonlat.spring.ai;

import com.alibaba.fastjson.JSON;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

/**
 * mcp；https://sai.baidu.com/zh/
 * 百度搜索；<a href="https://sai.baidu.com/zh/detail/e014c6ffd555697deabf00d058baf388">https://sai.baidu.com/zh/detail/e014c6ffd555697deabf00d058baf388</a>
 * key申请；<a href="https://console.bce.baidu.com/iam/?_=1753597622044#/iam/apikey/list">apikey</a>
 * 2025/7/27 14:29
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AiSearchMCPTest {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Test
    public void test_ollama_ai() {

        OllamaApi ollamaApi = OllamaApi.builder()
                .baseUrl("http://127.0.0.1:11434")
                .build();

        OllamaChatModel ollamaChatModel = OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder()
                        .model("qwen3:14B")
                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient()).getToolCallbacks())
                        .build())
                .build();

        ChatResponse call = ollamaChatModel.call(Prompt.builder().messages(new UserMessage("搜索github jasonlat-dot有哪些公开仓库")).build());
        log.info("测试结果:{}", JSON.toJSONString(call.getResult()));
    }

    @Test
    public void test_zhipu_ai() {

        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(
                "https://open.bigmodel.cn/api/paas",
                "c3e3912426c49a7b990280b1d2d235a3.tcgJOlShzn6ba6uk");

        ZhiPuAiChatModel zhiPuAiChatModel = new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                .model("GLM-4-Flash")
                .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient()).getToolCallbacks())
                .temperature(0.7)
                .build());

        ChatResponse call = zhiPuAiChatModel.call(Prompt.builder().messages(new UserMessage("https://github.com/jasonlat-dot/ecc-encrypt-springboot-starter 介绍一下")).build());
        log.info("测试结果:{}", JSON.toJSONString(call.getResult()));
    }


    @Test
    public void test() {

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder()
                        .baseUrl("https://dashscope.aliyuncs.com/compatible-mode")
                        .apiKey("sk-382688e6c9184dea8715dfbfdb241355")
//                        .completionsPath("v1/chat/completions")
//                        .embeddingsPath("v1/embeddings")
                        .build())
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("qwen-plus")
                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient()).getToolCallbacks())
                        .build())
                .build();

        ChatResponse call = chatModel.call(Prompt.builder().messages(new UserMessage("搜索github jasonlat-dot有哪些项目")).build());
        log.info("测试结果:{}", JSON.toJSONString(call.getResult()));
    }

    public McpSyncClient sseMcpClient() {
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport.builder("http://appbuilder.baidu.com/v2/ai_search/mcp/")
                .sseEndpoint("sse?api_key=bce-v3/ALTAK-DAjqmKUyljHZzKsZnJ8x6/88724397eb336d831b13364919cbade256c459a1")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMinutes(360)).build();
        var init_sse = mcpSyncClient.initialize();
        log.info("Tool SSE MCP Initialized {}", init_sse);

        return mcpSyncClient;
    }

}
