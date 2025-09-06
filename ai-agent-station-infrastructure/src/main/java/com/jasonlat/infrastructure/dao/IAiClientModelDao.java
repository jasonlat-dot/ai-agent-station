package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiClientModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * AI客户端模型配置表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiClientModelDao extends BaseMapper<AiClientModel> {

    /**
     * 根据模型ID查询聊天模型配置
     * @param modelId 模型ID
     * @return 聊天模型配置对象
     */
    AiClientModel queryByModelId(@Param("modelId") String modelId);
}
