package com.jasonlat.domain.agent.model.valobj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AiAgentEnumVO {

    AI_CLIENT_API("对话API", "api", "ai_client_api_", "ai_client_api_data_list", "aiClientApiLoadDataStrategy"),
    AI_CLIENT_MODEL("对话模型", "model", "ai_client_model_", "ai_client_model_data_list", "aiClientModelLoadDataStrategy"),
    AI_CLIENT_SYSTEM_PROMPT("提示词", "prompt", "ai_client_system_prompt_", "ai_client_system_prompt_data_list", "aiClientSystemPromptLoadDataStrategy"),
    AI_CLIENT_TOOL_MCP("mcp工具", "tool_mcp", "ai_client_tool_mcp_", "ai_client_tool_mcp_data_list", "aiClientToolMCPLoadDataStrategy"),
    AI_CLIENT_ADVISOR("顾问角色", "advisor", "ai_client_advisor_", "ai_client_advisor_data_list", "aiClientAdvisorLoadDataStrategy"),
    AI_CLIENT("客户端", "client", "ai_client_", "ai_client_data_list", "aiClientLoadDataStrategy"),

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
     * 数据名称
     */
    private String dynamicDataKey;

    /**
     * 装配数据策略
     */
    private String loadDataStrategy;


    /**
     * 根据code获取对应的枚举
     *
     * @param code 枚举code值
     * @return 对应的枚举，如果未找到则返回null
     */
    public static AiAgentEnumVO getByCode(String code) {
        if (!StringUtils.hasLength(code) || !StringUtils.hasLength(code.trim())) {
            throw new RuntimeException("code value is null or empty.");
        }
        for (AiAgentEnumVO enumVO : AiAgentEnumVO.values()) {
            if (code.equals(enumVO.getCode())) {
                return enumVO;
            }
        }
        throw new RuntimeException("code value " + code + " not exist!");
    }


    /**
     * 获取Bean名称
     *
     * @param id 传入的参数
     * @return beanNameTag + id 拼接的Bean名称
     */
    public String getBeanName(String id) {
        if (!StringUtils.hasLength(id) || !StringUtils.hasLength(id.trim())) {
            throw new RuntimeException("id value is null!");
        }
        return this.beanNameTag + id;
    }

}
