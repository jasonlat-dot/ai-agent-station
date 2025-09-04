package com.jasonlat.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * AI客户端统一关联配置表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_client_config")
public class AiClientConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 源类型（model、client）
     */
    @TableField("source_type")
    private String sourceType;

    /**
     * 源ID（如 chatModelId、chatClientId 等）
     */
    @TableField("source_id")
    private String sourceId;

    /**
     * 目标类型（api、client）
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 目标ID（如 openAiApiId、chatModelId、systemPromptId、advisorId等
     */
    @TableField("target_id")
    private String targetId;

    /**
     * 扩展参数（JSON格式）
     */
    @TableField("ext_param")
    private String extParam;

    /**
     * 状态(0:禁用,1:启用)
     */
    @TableField("status")
    private Boolean status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
