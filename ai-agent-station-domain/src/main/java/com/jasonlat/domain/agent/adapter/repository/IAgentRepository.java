package com.jasonlat.domain.agent.adapter.repository;

import com.jasonlat.domain.agent.model.valobj.*;

import java.util.List;
import java.util.Map;

/**
 * @author jasonlat
 */
public interface IAgentRepository {
    /**
     * 查询AI客户端API信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端API信息
     */
    List<AiClientApiVO> queryAiClientApiVOByClientIds(List<String> clientIdList);

    /**
     * 获取AI客户端模型信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端模型信息
     */
    List<AiClientModelVO> queryAiClientModelVOByClientIds(List<String> clientIdList);

    /**
     * 获取AI客户端工具MCP信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端工具MCP信息
     */
    List<AiClientToolMcpVO> queryAiClientToolMcpVOByClientIds(List<String> clientIdList);

    /**
     * 获取AI客户端系统提示信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端系统提示信息
     */
    List<AiClientSystemPromptVO> queryAiClientSystemPromptVOByClientIds(List<String> clientIdList);

    /**
     * 获取AI客户端系统提示信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端系统提示信息 Map结构 key是 PromptId
     */
    Map<String, AiClientSystemPromptVO> queryAiClientSystemPromptMapVOByClientIds(List<String> clientIdList);

    /**
     * 获取AI客户端顾问信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端顾问信息
     */
    List<AiClientAdvisorVO> queryAiClientAdvisorVOByClientIds(List<String> clientIdList);

    /**
     * 获取AI客户端信息
     * @param clientIdList AI客户端ID列表
     * @return AI客户端信息
     */
    List<AiClientVO> queryAiClientVOByClientIdList(List<String> clientIdList);

    /**
     * 获取AI客户端API信息
     * @param modelIdList AI模型ID列表
     * @return AI客户端API信息
     */
    List<AiClientApiVO> queryAiClientApiVOByModelIds(List<String> modelIdList);

    /**
     * 获取AI客户端模型信息
     * @param modelIdList AI模型ID列表
     * @return AI客户端模型信息
     */
    List<AiClientModelVO> queryAiClientModelVOByModelIds(List<String> modelIdList);

    /**
     * 获取AI agent客户端流程配置
     * @param aiAgentId agent ID
     * @return AI agent 客户端流程配置
     */
    Map<String, AiAgentClientFlowConfigVO> queryAiAgentClientFlowConfig(String aiAgentId);
}
