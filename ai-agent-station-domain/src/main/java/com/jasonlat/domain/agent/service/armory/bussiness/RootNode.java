package com.jasonlat.domain.agent.service.armory.bussiness;

import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.service.armory.bussiness.data.ILoadDataStrategy;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 根节点，数据加载
 */
@Slf4j
@Component
public class RootNode extends AbstractArmorySupport {

    @Resource
    private AiClientApiNode aiClientApiNode;

    /**
     * map 注入
     */
    protected final Map<String, ILoadDataStrategy> loadDataStrategyMap;

    public RootNode(Map<String, ILoadDataStrategy> loadDataStrategyMap) {
        this.loadDataStrategyMap = loadDataStrategyMap;
    }

    @Override
    protected void multiThread(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext)  {
        // 加载数据
        ILoadDataStrategy loadDataStrategy = loadDataStrategyMap.get(requestParameter.getLoadDataStrategy());
        if (null == loadDataStrategy) {
            throw new RuntimeException("loadDataStrategy is null, please check commandType is one of " + loadDataStrategyMap.keySet());
        }
        loadDataStrategy.loadData(requestParameter, dynamicContext);
    }

    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientApiNode;
    }
}
