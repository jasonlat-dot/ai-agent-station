package com.jasonlat.domain.agent.service.armory.bussiness.data;

import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;

public interface ILoadDataStrategy {

    /**
     * 加载数据
     * @param requestParameter 请求参数
     * @param dynamicContext 动态上下文
     */
    void loadData(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext);
}
