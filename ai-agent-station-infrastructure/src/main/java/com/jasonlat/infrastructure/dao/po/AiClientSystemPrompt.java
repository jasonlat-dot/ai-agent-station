package com.jasonlat.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.jasonlat.infrastructure.dao.po.base.BasePoSupport;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * AI客户端系统提示词配置表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_client_system_prompt")
public class AiClientSystemPrompt extends BasePoSupport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 系统提示词ID
     */
    @TableField("prompt_id")
    private String promptId;

    /**
     * 系统提示词名称
     */
    @TableField("prompt_name")
    private String promptName;

    /**
     * 系统提示词内容
     */
    @TableField("prompt_content")
    private String promptContent;

    /**
     * 系统提示词描述
     */
    @TableField("description")
    private String description;
}
