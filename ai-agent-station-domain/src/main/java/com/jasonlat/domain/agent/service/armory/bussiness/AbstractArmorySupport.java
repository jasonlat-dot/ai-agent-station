package com.jasonlat.domain.agent.service.armory.bussiness;

import com.jasonlat.design.framework.tree.AbstractMultiThreadStrategyRouter;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;
import com.jasonlat.types.utils.BeanUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class AbstractArmorySupport extends AbstractMultiThreadStrategyRouter<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> {

    private static final Logger log = LoggerFactory.getLogger(AbstractArmorySupport.class);

    @Resource
    protected BeanUtils beanUtils;

    @Override
    protected void multiThread(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext)  {
        // 缺省的方法, 子类按需实现
    }

    protected String beanName(String id) {
        return null;
    }

    protected String dynamicDataKey() {
        return null;
    }



}
