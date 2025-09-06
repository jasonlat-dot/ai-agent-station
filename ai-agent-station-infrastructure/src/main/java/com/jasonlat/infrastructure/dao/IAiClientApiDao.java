package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiClientApi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * AI客户端API配置表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiClientApiDao extends BaseMapper<AiClientApi> {

    /**
     * 根据API ID查询AI客户端API配置
     * @param apiId API ID
     * @return AI客户端API配置对象
     */
    AiClientApi queryByApiId(@Param("apiId") String apiId);
}
