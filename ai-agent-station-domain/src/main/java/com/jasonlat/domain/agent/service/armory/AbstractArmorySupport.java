package com.jasonlat.domain.agent.service.armory;

import com.jasonlat.design.framework.tree.AbstractMultiThreadStrategyRouter;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractArmorySupport extends AbstractMultiThreadStrategyRouter<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> {
    private static final Logger log = LoggerFactory.getLogger(AbstractArmorySupport.class);


    @Override
    protected void multiThread(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext)  {
        log.info("缺省的方法：AbstractArmorySupport.multiThread()");
    }
}
