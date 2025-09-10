package com.jasonlat.domain.agent.service.execute.auto;

import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ExecuteCommandEntity;
import com.jasonlat.domain.agent.service.execute.IExecuteStrategy;
import com.jasonlat.domain.agent.service.execute.auto.step.factory.DefaultAutoAgentExecuteStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author jasonlat
 * 2025-09-10  19:59
 */
@Slf4j
@Service
public class AutoExecuteStrategy implements IExecuteStrategy {

    @Resource
    private DefaultAutoAgentExecuteStrategyFactory defaultAutoAgentExecuteStrategyFactory;

    /**
     * 执行命令
     * @param executeCommandEntity 执行命令的实体参数
     */
    @Override
    public String execute(ExecuteCommandEntity executeCommandEntity) throws Exception {
        StrategyHandler<ExecuteCommandEntity, DefaultAutoAgentExecuteStrategyFactory.DynamicContext, String> executeHandler
                = defaultAutoAgentExecuteStrategyFactory.executeStrategyHandler();
        return  executeHandler.apply(executeCommandEntity, new DefaultAutoAgentExecuteStrategyFactory.DynamicContext());
    }
}
