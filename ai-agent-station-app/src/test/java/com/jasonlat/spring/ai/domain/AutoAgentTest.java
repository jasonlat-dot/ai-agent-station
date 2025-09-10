package com.jasonlat.spring.ai.domain;


import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.model.entity.ExecuteCommandEntity;
import com.jasonlat.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.jasonlat.domain.agent.service.armory.DefaultArmoryStrategy;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;
import com.jasonlat.domain.agent.service.execute.auto.AutoExecuteStrategy;
import com.jasonlat.domain.agent.service.execute.auto.step.factory.DefaultAutoAgentExecuteStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AutoAgentTest {

    @Autowired
    private DefaultArmoryStrategy defaultArmoryStrategy;

    @Autowired
    private AutoExecuteStrategy autoExecuteStrategy;

    @Resource
    private ApplicationContext applicationContext;

    @Before
    public void init() throws Exception {
        String armory = defaultArmoryStrategy.armory(ArmoryCommandEntity.builder()
                .commandType(AiAgentEnumVO.AI_CLIENT.getCode())
                .commandIdList(Arrays.asList("3101", "3102", "3103"))
                .build());
        log.info("armory: {}", armory);

        ChatClient chatClient = (ChatClient) applicationContext.getBean(AiAgentEnumVO.AI_CLIENT.getBeanName("3101"));
        log.info("客户端构建:{}", chatClient);
    }

    @Test
    public void autoAgent() throws Exception {
        ExecuteCommandEntity executeCommandEntity = new ExecuteCommandEntity();
        executeCommandEntity.setAiAgentId("3");
        executeCommandEntity.setMessage("搜索小傅哥，技术项目列表。编写成一份文档，说明不同项目的学习目标，以及不同阶段的伙伴应该学习哪个项目。");
        executeCommandEntity.setSessionId("session-id-" + System.currentTimeMillis());
        executeCommandEntity.setMaxStep(3);

        String execute = autoExecuteStrategy.execute(executeCommandEntity);
        log.info("测试结果:{}", execute);
    }

}
