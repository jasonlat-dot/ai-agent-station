package com.jasonlat.infrastructure.adapter.repository;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasonlat.domain.agent.adapter.repository.IAgentRepository;
import com.jasonlat.domain.agent.model.valobj.*;
import com.jasonlat.infrastructure.dao.*;
import com.jasonlat.infrastructure.dao.po.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jasonlat.domain.agent.model.valobj.AiAgentEnumVO.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AgentRepository implements IAgentRepository {

    private final IAiClientConfigDao aiClientConfigDao;
    private final IAiClientModelDao aiClientModelDao;
    private final IAiClientApiDao aiClientApiDao;
    private final IAiClientToolMcpDao aiClientToolMcpDao;
    private final IAiClientSystemPromptDao aiClientSystemPromptDao;
    private final IAiClientAdvisorDao aiClientAdvisorDao;
    private final IAiClientDao aiClientDao;

    /**
     * 查询AI客户端API信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端API信息
     */
    @Override
    public List<AiClientApiVO> queryAiClientApiVOByClientIds(List<String> clientIdList) {
        if (null == clientIdList || clientIdList.isEmpty()) return List.of();

        // 构建AI客户端API返回信息
        List<AiClientApiVO> result = new ArrayList<>();
        // 遍历AI客户端ID列表
        Set<String> apiIdSet = new HashSet<>();
        Set<String> clientIdSet = new HashSet<>(clientIdList);
        clientIdSet.forEach(clientId -> {
            // 通过clientId查询关联的modelId
            List<AiClientConfig> clientConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT.getCode(), clientId);
            // 遍历配置
            clientConfigs.forEach(clientConfig -> {
                if (AI_CLIENT_MODEL.getCode().equals(clientConfig.getTargetType()) && clientConfig.isAvailable()) {
                    // 获取关联的modelId
                    String modelId = clientConfig.getTargetId();
                    // 通过modelId查询AI模型信息, 获取api_id
                    AiClientModel model = aiClientModelDao.queryByModelId(modelId);
                    if (null != model && model.isAvailable()) {
                        String apiId = model.getApiId();
                        if (apiIdSet.contains(apiId)) return;
                        apiIdSet.add(apiId);

                        // 通过apiId查询AI模型信息
                        AiClientApi aiClientApi = aiClientApiDao.queryByApiId(apiId);
                        if (null != aiClientApi && aiClientApi.isAvailable()) {
                            // 构建AI客户端API信息
                            AiClientApiVO aiClientApiVO = AiClientApiVO.builder()
                                    .apiId(aiClientApi.getApiId())
                                    .baseUrl(aiClientApi.getBaseUrl())
                                    .apiKey(aiClientApi.getApiKey())
                                    .completionsPath(aiClientApi.getCompletionsPath())
                                    .embeddingsPath(aiClientApi.getEmbeddingsPath())
                                    .build();

                            // 避免重复数据
                            result.add(aiClientApiVO);
                        }
                    }
                }
            });
        });
        return result;
    }

    /**
     * 获取AI客户端模型信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端模型信息
     */
    @Override
    public List<AiClientModelVO> queryAiClientModelVOByClientIds(List<String> clientIdList) {
        if (null == clientIdList || clientIdList.isEmpty()) return List.of();
        List<AiClientModelVO> result = new ArrayList<>();

        Set<String> modelIdSet = new HashSet<>();
        Set<String> clientIdSet = new HashSet<>(clientIdList);
        clientIdSet.forEach(clientId -> {
            // 通过clientId查询关联的modelId
            List<AiClientConfig> clientConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT.getCode(), clientId);

            clientConfigs.forEach(clientConfig -> {
                if (AI_CLIENT_MODEL.getCode().equals(clientConfig.getTargetType()) && clientConfig.isAvailable()) {
                    // 获取关联的modelId
                    String modelId = clientConfig.getTargetId();
                    if (modelIdSet.contains(modelId)) return;
                    modelIdSet.add(modelId);

                    // 通过modelId查询AI模型信息
                    AiClientModel model = aiClientModelDao.queryByModelId(modelId);
                    if (null != model && model.isAvailable()) {
                        // 查询模型需要的 mcp_tools 信息
                        List<AiClientConfig> clientModelConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT_MODEL.getCode(), modelId);
                        List<String> toolMcpIds = clientModelConfigs
                                .stream().filter(clientModelConfig ->
                                        AI_CLIENT_TOOL_MCP.getCode().equals(clientModelConfig.getTargetType()) && clientModelConfig.isAvailable())
                                .map(AiClientConfig::getTargetId).toList();

                        // 构建AI客户端模型信息
                        AiClientModelVO aiClientModelVO = AiClientModelVO.builder()
                                .modelId(model.getModelId())
                                .modelName(model.getModelName())
                                .modelType(model.getModelType())
                                .apiId(model.getApiId())
                                .toolMcpIds(toolMcpIds)
                                .build();

                        // 避免重复数据
                        result.add(aiClientModelVO);
                    }
                }
            });
        });
        return result;
    }

    /**
     * 获取AI客户端工具MCP信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端工具MCP信息
     */
    @Override
    public List<AiClientToolMcpVO> queryAiClientToolMcpVOByClientIds(List<String> clientIdList) {
        if (null == clientIdList || clientIdList.isEmpty()) return List.of();

        List<AiClientToolMcpVO> result = new ArrayList<>();
        Set<String> mcpIdSet = new HashSet<>();

        Set<String> clientIdSet = new HashSet<>(clientIdList);
        clientIdSet.forEach(clientId -> {
            List<AiClientConfig> clientConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT.getCode(), clientId);

            clientConfigs.forEach(clientConfig -> {
                if (AI_CLIENT_MODEL.getCode().equals(clientConfig.getTargetType())) {
                    String modelId = clientConfig.getTargetId();
                    // 通过modelId查询关联的tool_mcp配置
                    List<AiClientConfig> modelConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT_MODEL.getCode(), modelId);

                    modelConfigs.forEach(modelConfig -> {
                        if (AI_CLIENT_TOOL_MCP.getCode().equals(modelConfig.getTargetType()) && clientConfig.isAvailable()) {
                            // 获取关联的 mcpId
                            String mcpId = modelConfig.getTargetId();
                            if (mcpIdSet.contains(mcpId)) return;
                            mcpIdSet.add(mcpId);

                            // 通过 mcpId 查询AI模型信息
                            AiClientToolMcp mcp = aiClientToolMcpDao.queryByMcpId(mcpId);
                            if (null != mcp && mcp.isAvailable()) {

                                // 构建AI客户端模型信息
                                AiClientToolMcpVO aiClientToolMcpVO = AiClientToolMcpVO.builder()
                                        .mcpId(mcp.getMcpId())
                                        .mcpName(mcp.getMcpName())
                                        .transportType(mcp.getTransportType())
                                        .transportConfig(mcp.getTransportConfig())
                                        .requestTimeout(mcp.getRequestTimeout())
                                        .build();

                                String transportConfig = mcp.getTransportConfig();
                                String transportType = mcp.getTransportType();

                                try {
                                    if ("sse".equals(transportType)) {
                                        // 解析SSE配置
                                        AiClientToolMcpVO.TransportConfigSse transportConfigSse = JSON.parseObject(transportConfig, AiClientToolMcpVO.TransportConfigSse.class);
                                        aiClientToolMcpVO.setTransportConfigSse(transportConfigSse);
                                    } else if ("stdio".equals(transportType)) {
                                        // 解析STDIO配置
                                        Map<String, AiClientToolMcpVO.TransportConfigStdio.Stdio> stdio = JSON.parseObject(
                                                transportConfig,
                                                new TypeReference<>() {}
                                        );

                                        AiClientToolMcpVO.TransportConfigStdio transportConfigStdio = new AiClientToolMcpVO.TransportConfigStdio();
                                        transportConfigStdio.setStdio(stdio);

                                        aiClientToolMcpVO.setTransportConfigStdio(transportConfigStdio);
                                    }
                                } catch (Exception e) {
                                    log.error("解析传输配置失败: {}", e.getMessage(), e);
                                }


                                // 避免重复数据
                                result.add(aiClientToolMcpVO);
                            }
                        }
                    });
                }
            });
        });
        return result;
    }

    /**
     * 获取AI客户端系统提示信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端系统提示信息
     */
    @Override
    public List<AiClientSystemPromptVO> queryAiClientSystemPromptVOByClientIds(List<String> clientIdList) {
        if (null == clientIdList || clientIdList.isEmpty()) return List.of();

        List<AiClientSystemPromptVO> result = new ArrayList<>();
        Set<String> promptIdSet = new HashSet<>();

        Set<String> clientIdSet = new HashSet<>(clientIdList);
        clientIdSet.forEach(clientId -> {
            List<AiClientConfig> clientConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT.getCode(), clientId);
            // 通过clientId查询关联的promptId
            clientConfigs.forEach(clientConfig -> {
                if (AI_CLIENT_SYSTEM_PROMPT.getCode().equals(clientConfig.getTargetType()) && clientConfig.isAvailable()) {
                    // 获取关联的promptId
                    String promptId = clientConfig.getTargetId();

                    if (promptIdSet.contains(promptId)) return;
                    promptIdSet.add(promptId);

                    // 通过promptId查询AI模型信息
                    AiClientSystemPrompt systemPrompt = aiClientSystemPromptDao.queryByPromptId(promptId);
                    if (null != systemPrompt && systemPrompt.isAvailable()) {
                        // 构建AI客户端模型信息
                        AiClientSystemPromptVO aiClientSystemPromptVO = AiClientSystemPromptVO.builder()
                                .promptId(systemPrompt.getPromptId())
                                .promptName(systemPrompt.getPromptName())
                                .promptContent(systemPrompt.getPromptContent())
                                .description(systemPrompt.getDescription())
                                .build();

                        result.add(aiClientSystemPromptVO);
                    }
                }
            });
        });
        return result;
    }

    @Override
    public Map<String, AiClientSystemPromptVO> queryAiClientSystemPromptMapVOByClientIds(List<String> clientIdList) {
        List<AiClientSystemPromptVO> aiClientSystemPrompts = queryAiClientSystemPromptVOByClientIds(clientIdList);
        if (null == aiClientSystemPrompts || aiClientSystemPrompts.isEmpty()) {
            return Map.of();
        }

        // 将PO对象转换为VO对象，并构建Map结构
        return aiClientSystemPrompts.stream()
                .map(prompt -> AiClientSystemPromptVO.builder()
                        .promptId(prompt.getPromptId())
                        .promptContent(prompt.getPromptContent())
                        .build())
                .collect(Collectors.toMap(
                        AiClientSystemPromptVO::getPromptId,  // key: id
                        prompt -> prompt,               // value: AiClientSystemPromptVO对象
                        (existing, replacement) -> existing  // 如果有重复key，保留第一个
                ));
    }

    /**
     * 获取AI客户端顾问信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端顾问信息
     */
    @Override
    public List<AiClientAdvisorVO> queryAiClientAdvisorVOByClientIds(List<String> clientIdList) {
        if (null == clientIdList || clientIdList.isEmpty()) return List.of();

        List<AiClientAdvisorVO> result = new ArrayList<>();
        Set<String> advisorIdSet = new HashSet<>();

        Set<String> clientIdSet = new HashSet<>(clientIdList);
        clientIdSet.forEach(clientId -> {
            List<AiClientConfig> clientConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT.getCode(), clientId);
            clientConfigs.forEach(clientConfig -> {
                if (!AI_CLIENT_ADVISOR.getCode().equals(clientConfig.getTargetType()) || !clientConfig.isAvailable()) {
                    return;
                }
                String advisorId = clientConfig.getTargetId();
                if (advisorIdSet.contains(advisorId)) return;
                advisorIdSet.add(advisorId);

                // 查询AI顾问信息
                AiClientAdvisor aiClientAdvisor = aiClientAdvisorDao.queryByAdvisorId(advisorId);
                if (null == aiClientAdvisor || !aiClientAdvisor.isAvailable()) {
                    return;
                }

                // 解析 extParam 配置
                AiClientAdvisorVO.ChatMemory chatMemory = null;
                AiClientAdvisorVO.RagAnswer ragAnswer = null;

                String extParam = aiClientAdvisor.getExtParam();
                if (null != extParam && !extParam.trim().isEmpty()) {
                    try {
                        if (AiClientAdvisorTypeEnumVO.CHAT_MEMORY.getCode().equals(aiClientAdvisor.getAdvisorType())) {
                            chatMemory = JSON.parseObject(extParam, AiClientAdvisorVO.ChatMemory.class);
                        } else if (AiClientAdvisorTypeEnumVO.RAG_ANSWER.getCode().equals(aiClientAdvisor.getAdvisorType())) {
                            ragAnswer = JSON.parseObject(extParam, AiClientAdvisorVO.RagAnswer.class);
                        }
                    } catch (Exception exception) {
                        throw new RuntimeException("解析 extParam 配置失败");
                    }
                }

                // 构建AI客户端顾问信息
                AiClientAdvisorVO aiClientAdvisorVO = AiClientAdvisorVO.builder()
                        .advisorId(aiClientAdvisor.getAdvisorId())
                        .advisorName(aiClientAdvisor.getAdvisorName())
                        .advisorType(aiClientAdvisor.getAdvisorType())
                        .orderNum(aiClientAdvisor.getOrderNum())
                        .chatMemory(chatMemory)
                        .ragAnswer(ragAnswer)
                        .build();

                result.add(aiClientAdvisorVO);
            });
        });
        return result;
    }


    /**
     * 获取AI客户端信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端信息
     */
    @Override
    public List<AiClientVO> queryAiClientVOByClientIdList(List<String> clientIdList) {
        if (null == clientIdList || clientIdList.isEmpty()) return List.of();

        List<AiClientVO> result = new ArrayList<>();

        Set<String> clientIdSet = new HashSet<>(clientIdList);
        clientIdSet.forEach(clientId -> {
            // 查询客户端基本信息
            AiClient aiClient = aiClientDao.queryByClientId(clientId);
            if (null == aiClient || !aiClient.isAvailable()) {
                return;
            }

            // 查询客户端配置信息
            List<AiClientConfig> clientConfigs = aiClientConfigDao.queryBySourceTypeAndId(AI_CLIENT.getCode(), clientId);

            String modelId = null;
            List<String> promptIdList = new ArrayList<>();
            List<String> mcpIdList = new ArrayList<>();
            List<String> advisorIdList = new ArrayList<>();

            for (AiClientConfig clientConfig : clientConfigs) {
                if (!clientConfig.isAvailable()) {
                    continue;
                }

                switch (clientConfig.getTargetType()) {
                    case "model":
                        modelId = clientConfig.getTargetId();
                        if (null == modelId) {
                            throw new RuntimeException("AI客户端【clientId is + " + clientId + "】配置错误: client未正确配置model模型");
                        }
                        break;
                    case "prompt":
                        promptIdList.add(clientConfig.getTargetId());
                        break;
                    case "tool_mcp":
                        mcpIdList.add(clientConfig.getTargetId());
                        break;
                    case "advisor":
                        advisorIdList.add(clientConfig.getTargetId());
                        break;
                }
            }


            // 构建AI客户端信息
            AiClientVO aiClientVO = AiClientVO.builder()
                    .clientId(aiClient.getClientId())
                    .clientName(aiClient.getClientName())
                    .description(aiClient.getDescription())
                    .modelId(modelId)
                    .promptIdList(promptIdList)
                    .mcpIdList(mcpIdList)
                    .advisorIdList(advisorIdList)
                    .build();

            result.add(aiClientVO);
        });

        return result;
    }


    /**
     * 获取AI客户端API信息
     * @param modelIdList AI模型ID列表
     * @return AI客户端API信息
     */
    @Override
    public List<AiClientApiVO> queryAiClientApiVOByModelIds(List<String> modelIdList) {
        if (null == modelIdList || modelIdList.isEmpty()) return List.of();

        Set<String> apiIdSet = new HashSet<>();
        List<AiClientApiVO> result = new ArrayList<>();

        Set<String> modelIds = new HashSet<>(modelIdList);
        modelIds.forEach(modelId -> {
            // 通过 modelId 查询 AI模型信息， 获取apiId
            AiClientModel aiClientModel = aiClientModelDao.queryByModelId(modelId);
            if (null == aiClientModel || !aiClientModel.isAvailable()) {
                return;
            }
            // 获取 apiId
            String apiId = aiClientModel.getApiId();
            if (apiIdSet.contains(modelId)) return;
            apiIdSet.add(modelId);

            AiClientApi aiClientApi = aiClientApiDao.queryByApiId(apiId);
            if (null == aiClientApi || !aiClientApi.isAvailable()) {
                return;
            }
            // 构建AI客户端API信息
            AiClientApiVO aiClientApiVO = AiClientApiVO.builder()
                    .apiId(aiClientApi.getApiId())
                    .baseUrl(aiClientApi.getBaseUrl())
                    .apiKey(aiClientApi.getApiKey())
                    .completionsPath(aiClientApi.getCompletionsPath())
                    .embeddingsPath(aiClientApi.getEmbeddingsPath())
                    .build();

            result.add(aiClientApiVO);
        });
        return result;
    }

    /**
     * 获取AI客户端模型信息
     * @param modelIdList AI模型ID列表
     * @return AI客户端模型信息
     */
    @Override
    public List<AiClientModelVO> queryAiClientModelVOByModelIds(List<String> modelIdList) {
        if (null == modelIdList || modelIdList.isEmpty()) return List.of();

        List<AiClientModelVO> result = new ArrayList<>();

        Set<String> modelIds = new HashSet<>(modelIdList);
        modelIds.forEach(modelId -> {
            AiClientModel model = aiClientModelDao.queryByModelId(modelId);
            if (null == model || !model.isAvailable()) {
                return;
            }
            // 转换为VO
            AiClientModelVO modelVO = AiClientModelVO.builder()
                    .modelId(model.getModelId())
                    .apiId(model.getApiId())
                    .modelName(model.getModelName())
                    .modelType(model.getModelType())
                    .build();

            result.add(modelVO);
        });
        return result;
    }
}
