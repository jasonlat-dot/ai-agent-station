package com.jasonlat.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;

import com.jasonlat.infrastructure.dao.po.base.BasePoSupport;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * AI智能体配置表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_agent")
public class AiAgent extends BasePoSupport implements Serializable {

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
     * 智能体名称
     */
    @TableField("agent_name")
    private String agentName;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 渠道类型(agent，chat_stream)
     */
    @TableField("channel")
    private String channel;

}
