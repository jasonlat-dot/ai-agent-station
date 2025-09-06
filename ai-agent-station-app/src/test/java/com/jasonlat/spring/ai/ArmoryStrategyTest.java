package com.jasonlat.spring.ai;


import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArmoryStrategyTest {

    @Autowired
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;

    @Test
    public void testArmoryStrategy() throws Exception {

        List<String> clientIdList = List.of("3001");

        StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> strategyHandler = defaultArmoryStrategyFactory.strategyHandler();
        strategyHandler.apply(ArmoryCommandEntity.builder().commandType("aiClientLoadDataStrategy").commandIdList(clientIdList).build(), new DefaultArmoryStrategyFactory.DynamicContext());
    }

}
