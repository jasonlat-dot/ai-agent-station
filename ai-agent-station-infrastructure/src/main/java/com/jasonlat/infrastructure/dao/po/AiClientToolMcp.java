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
 * AI客户端MCP工具配置表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_client_tool_mcp")
public class AiClientToolMcp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * MCP工具ID
     */
    @TableField("mcp_id")
    private String mcpId;

    /**
     * MCP工具名称
     */
    @TableField("mcp_name")
    private String mcpName;

    /**
     * MCP传输类型：sse/stdio
     */
    @TableField("transport_type")
    private String transportType;

    /**
     * MCP传输配置(sse/stdio config)
     */
    @TableField("transport_config")
    private String transportConfig;

    /**
     * MCP请求超时时间(秒)
     */
    @TableField("request_timeout")
    private Integer requestTimeout;

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
