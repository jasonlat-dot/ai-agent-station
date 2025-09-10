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
 * 智能体-客户端关联表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_agent_flow_config")
public class AiAgentFlowConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 智能体ID
     */
    @TableField("agent_id")
    private String agentId;

    /**
     * 客户端ID
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 客户端名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 客户端类型
     */
    @TableField("client_type")
    private String clientType;

    /**
     * 序列号(执行顺序)
     */
    @TableField("sequence")
    private Integer sequence;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
