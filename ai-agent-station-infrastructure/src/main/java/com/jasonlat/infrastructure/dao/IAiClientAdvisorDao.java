package com.jasonlat.infrastructure.dao;

import com.jasonlat.infrastructure.dao.po.AiClientAdvisor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * AI客户端顾问配置表 Mapper 接口
 * </p>
 *
 * @author jasonlat
 * @since 2025-09-04
 */
public interface IAiClientAdvisorDao extends BaseMapper<AiClientAdvisor> {

    /**
     * 根据顾问ID查询顾问配置
     * @param advisorId 顾问ID
     * @return 顾问配置对象
     */
    AiClientAdvisor queryByAdvisorId(@Param("advisorId") String advisorId);
}
