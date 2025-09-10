package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiAgentFlowConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 智能体-客户端关联表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiAgentFlowConfigDao extends BaseMapper<AiAgentFlowConfig> {

    /**
     * 根据智能体ID查询关联配置列表
     * @param agentId 智能体ID
     * @return 智能体-客户端关联配置列表
     */
    List<AiAgentFlowConfig> queryByAgentId(@Param("agentId") String agentId);
}
