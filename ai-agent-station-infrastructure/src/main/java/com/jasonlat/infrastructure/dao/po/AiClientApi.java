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
 * AI客户端API配置表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_client_api")
public class AiClientApi extends BasePoSupport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 全局唯一配置ID
     */
    @TableField("api_id")
    private String apiId;

    /**
     * API基础URL
     */
    @TableField("base_url")
    private String baseUrl;

    /**
     * API密钥
     */
    @TableField("api_key")
    private String apiKey;

    /**
     * 补全对话API路径
     */
    @TableField("completions_path")
    private String completionsPath;

    /**
     * 嵌入向量API路径
     */
    @TableField("embeddings_path")
    private String embeddingsPath;
}
