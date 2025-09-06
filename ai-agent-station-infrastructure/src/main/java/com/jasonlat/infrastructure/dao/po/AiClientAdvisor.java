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
 * AI客户端顾问配置表
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
@Getter
@Setter
@TableName("ai_client_advisor")
public class AiClientAdvisor extends BasePoSupport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 顾问ID
     */
    @TableField("advisor_id")
    private String advisorId;

    /**
     * 顾问名称
     */
    @TableField("advisor_name")
    private String advisorName;

    /**
     * 顾问类型(PromptChatMemory/RagAnswer/SimpleLoggerAdvisor等)
     */
    @TableField("advisor_type")
    private String advisorType;

    /**
     * 顺序号
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 扩展参数配置，json 记录
     */
    @TableField("ext_param")
    private String extParam;
}
