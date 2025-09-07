package com.jasonlat.domain.agent.service.armory.bussiness.data.impl;

import com.jasonlat.domain.agent.adapter.repository.IAgentRepository;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.model.valobj.*;
import com.jasonlat.domain.agent.service.armory.bussiness.data.ILoadDataStrategy;
import com.jasonlat.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RequiredArgsConstructor
@Service("aiClientLoadDataStrategy")
public class AiClientLoadDataStrategy implements ILoadDataStrategy {

    private final IAgentRepository agentRepository;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void loadData(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) {
        List<String> clientIdList = armoryCommandEntity.getCommandIdList();

        // 异步获取数据
        CompletableFuture<List<AiClientApiVO>> aiClientApiFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询AiClient配置信息(ai_client_api), clientIdList: {} ", clientIdList);
            return agentRepository.queryAiClientApiVOByClientIds(clientIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AiClientModelVO>> aiClientModelFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询AiClient配置信息(ai_client_model), clientIdList: {} ", clientIdList);
            return agentRepository.queryAiClientModelVOByClientIds(clientIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AiClientToolMcpVO>> aiClientToolMcpFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询AiClient配置信息(ai_client_tool_mcp), clientIdList: {} ", clientIdList);
            return agentRepository.queryAiClientToolMcpVOByClientIds(clientIdList);
        }, threadPoolExecutor);

        CompletableFuture<Map<String,AiClientSystemPromptVO >> aiClientSystemPromptFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询AiClient配置信息(ai_client_system_prompt), clientIdList: {} ", clientIdList);
            return agentRepository.queryAiClientSystemPromptMapVOByClientIds(clientIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AiClientAdvisorVO>> aiClientAdvisorFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询AiClient配置信息(ai_client_advisor), clientIdList: {} ", clientIdList);
            return agentRepository.queryAiClientAdvisorVOByClientIds(clientIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AiClientVO>> aiClientFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询AiClient配置信息(ai_client), clientIdList: {} ", clientIdList);
            return agentRepository.queryAiClientVOByClientIdList(clientIdList);
        }, threadPoolExecutor);

        CompletableFuture.allOf(aiClientApiFuture).thenRun(() -> {
            dynamicContext.setValue(AiAgentEnumVO.AI_CLIENT_API.getDynamicDataKey(), aiClientApiFuture.join());
            dynamicContext.setValue(AiAgentEnumVO.AI_CLIENT_MODEL.getDynamicDataKey(), aiClientModelFuture.join());
            dynamicContext.setValue(AiAgentEnumVO.AI_CLIENT_SYSTEM_PROMPT.getDynamicDataKey(), aiClientSystemPromptFuture.join());
            dynamicContext.setValue(AiAgentEnumVO.AI_CLIENT_TOOL_MCP.getDynamicDataKey(), aiClientToolMcpFuture.join());
            dynamicContext.setValue(AiAgentEnumVO.AI_CLIENT_ADVISOR.getDynamicDataKey(), aiClientAdvisorFuture.join());
            dynamicContext.setValue(AiAgentEnumVO.AI_CLIENT.getDynamicDataKey(), aiClientFuture.join());

        }).join();
    }
}
