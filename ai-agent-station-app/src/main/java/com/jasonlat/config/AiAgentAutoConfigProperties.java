package com.jasonlat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author jasonlat
 * 2025-09-12  21:10
 */
@Data
@ConfigurationProperties(prefix = "spring.ai.agent.auto-config")
public class AiAgentAutoConfigProperties {

    /**
     * 是否启用AI Agent自动装配
     */
    private boolean enabled = false;

    /**
     * 需要自动装配的客户端ID列表
     */
    private List<String> clientIds;

}
