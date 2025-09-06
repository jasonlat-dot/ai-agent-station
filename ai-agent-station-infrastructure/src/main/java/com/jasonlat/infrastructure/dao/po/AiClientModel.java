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
 * AI客户端模型配置表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_client_model")
public class AiClientModel extends BasePoSupport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 全局唯一模型ID
     */
    @TableField("model_id")
    private String modelId;

    /**
     * 关联的API配置ID -》 表示对应的OpenAiApi使用哪个模型
     */
    @TableField("api_id")
    private String apiId;

    /**
     * 模型名称（deepseek-r1:14B、qwen3-flash)
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 模型类型：openai、deepseek、claude
     */
    @TableField("model_type")
    private String modelType;
}
