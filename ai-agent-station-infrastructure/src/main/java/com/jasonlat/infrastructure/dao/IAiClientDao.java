package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiClient;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * AI客户端配置表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiClientDao extends BaseMapper<AiClient> {

    /**
     * 根据客户端ID查询AI客户端配置
     * @param clientId 客户端ID
     * @return AI客户端配置对象
     */
    AiClient queryByClientId(@Param("clientId") String clientId);
}
