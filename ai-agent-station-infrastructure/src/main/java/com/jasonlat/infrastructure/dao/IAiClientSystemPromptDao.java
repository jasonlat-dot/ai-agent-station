package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiClientSystemPrompt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * AI客户端系统提示词配置表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiClientSystemPromptDao extends BaseMapper<AiClientSystemPrompt> {

    /**
     * 根据提示词ID查询系统提示词配置
     */
    AiClientSystemPrompt queryByPromptId(@Param("promptId") String promptId);
}
