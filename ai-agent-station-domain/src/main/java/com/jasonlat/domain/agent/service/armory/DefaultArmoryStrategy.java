package com.jasonlat.domain.agent.service.armory;

import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author jasonlat
 * 2025-09-10  21:38
 */
@Service
public class DefaultArmoryStrategy implements IArmoryStrategy {

    @Resource
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;

    /**
     * 策略执行
     * @param armoryCommandEntity 策略执行参数
     * @throws Exception 策略执行过程中可能抛出的异常
     */
    @Override
    public String armory(ArmoryCommandEntity armoryCommandEntity) throws Exception {
        StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> armoryStrategyHandler
                = defaultArmoryStrategyFactory.armoryStrategyHandler();
        return armoryStrategyHandler.apply(armoryCommandEntity, new DefaultArmoryStrategyFactory.DynamicContext());
    }
}
