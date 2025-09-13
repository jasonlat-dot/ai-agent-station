package com.jasonlat.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoAgentRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * AI智能体ID
     */
    @NotBlank(message = "AI智能体ID不能为空")
    private String aiAgentId;

    /**
     * 用户消息
     */
    @NotBlank(message = "用户消息不能为空")
    private String message;

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 最大执行步数
     */
    @Max(value = 20)
    @Min(value = 1)
    private Integer maxStep;
}