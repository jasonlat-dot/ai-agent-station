package com.jasonlat.domain.agent.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 命令执行实体
 * @author jasonlat
 * 2025-09-10  19:57
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
