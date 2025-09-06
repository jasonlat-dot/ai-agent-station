package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiClientConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * AI客户端统一关联配置表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiClientConfigDao extends BaseMapper<AiClientConfig> {

    /**
     * 根据源类型和源ID查询AI客户端配置
     * @param sourceType 源类型
     * @param sourceId 源ID
     * @return AI客户端配置对象列表
     */
    List<AiClientConfig> queryBySourceTypeAndId(@Param("sourceType") String sourceType, @Param("sourceId") String sourceId);
}
