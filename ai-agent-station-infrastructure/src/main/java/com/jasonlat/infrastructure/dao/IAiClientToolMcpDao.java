package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiClientToolMcp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * AI客户端MCP工具配置表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiClientToolMcpDao extends BaseMapper<AiClientToolMcp> {

    /**
     * 根据MCP ID查询MCP客户端配置
     * @param mcpId MCP ID
     * @return MCP客户端配置对象
     */
    AiClientToolMcp queryByMcpId(@Param("mcpId") String mcpId);
}
