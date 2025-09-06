package com.jasonlat.domain.agent.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AiAgentEnumVO {

    AI_CLIENT("客户端", "client", "ai_client_", "aiClientLoadDataStrategy"),
    AI_CLIENT_MODEL("对话模型", "model", "ai_client_model_", "aiClientModelLoadDataStrategy"),
    // aiClientToolMcpLoadDataStrategy 暂未实现
    AI_CLIENT_TOOL_MCP("工具MCP", "tool_mcp", "ai_client_tool_mcp_", "aiClientToolMcpLoadDataStrategy"),
    // aiClientSystemPromptLoadDataStrategy 暂未实现
    AI_CLIENT_SYSTEM_PROMPT("系统提示语", "prompt", "ai_client_system_prompt_", "aiClientSystemPromptLoadDataStrategy"),
    // aiClientAdvisorLoadDataStrategy 暂未实现
    AI_CLIENT_ADVISOR("顾问", "advisor", "ai_client_advisor_", "aiClientAdvisorLoadDataStrategy"),

    ;

    /**
     * 名称
     */
    private String name;

    /**
     * code
     */
    private String code;

    /**
     * Bean 对象名称标签
     */
    private String beanNameTag;

    /**
     * 装配数据策略
     */
    private String loadDataStrategy;


}
