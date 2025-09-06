package com.jasonlat.domain.agent.service.armory.bussiness.data.impl;

import com.jasonlat.domain.agent.adapter.repository.IAgentRepository;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.model.valobj.AiClientApiVO;
import com.jasonlat.domain.agent.model.valobj.AiClientModelVO;
import com.jasonlat.domain.agent.service.armory.bussiness.data.ILoadDataStrategy;
import com.jasonlat.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
@RequiredArgsConstructor
@Service("aiClientModelLoadDataStrategy")
public class AiClientModelLoadDataStrategy implements ILoadDataStrategy {

    private final IAgentRepository agentRepository;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void loadData(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) {
        List<String> modelIdList = armoryCommandEntity.getCommandIdList();

        CompletableFuture<List<AiClientApiVO>> aiClientApiListFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询配置数据(ai_client_api) {}", modelIdList);
            return agentRepository.queryAiClientApiVOByModelIds(modelIdList);
        }, threadPoolExecutor);

        CompletableFuture<List<AiClientModelVO>> aiClientModelListFuture = CompletableFuture.supplyAsync(() -> {
            log.info("查询配置数据(ai_client_model) {}", modelIdList);
            return agentRepository.queryAiClientModelVOByModelIds(modelIdList);
        }, threadPoolExecutor);
    }
}
