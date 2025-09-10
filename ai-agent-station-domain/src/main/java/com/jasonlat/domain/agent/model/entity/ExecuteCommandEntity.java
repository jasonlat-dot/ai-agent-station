package com.jasonlat.domain.agent.model.entity;

import lombok.Data;

/**
 * 命令执行实体
 * @author jasonlat
 * 2025-09-10  19:57
 */
@Data
public class ExecuteCommandEntity {

    /**
     * AI代理ID
     */
    private String aiAgentId;

    /**
     * 用户输入信息
     */
    private String message;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 最大执行步骤
     */
    private Integer maxStep;
}
