package com.jasonlat.domain.agent.service.armory;


import com.alibaba.fastjson2.JSON;
import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.model.valobj.AiAgentEnumVO;
import com.jasonlat.domain.agent.model.valobj.AiClientApiVO;
import com.jasonlat.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AiClientApiNode extends AbstractArmorySupport {

    private static final Logger log = LoggerFactory.getLogger(AiClientApiNode.class);

    @Resource
    private AiClientToolMcpNode aiClientToolMcpNode;

    @Override
    protected String doApply(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai agent API 构建节点: {}", JSON.toJSONString(armoryCommandEntity));

        // 从缓存中获取数据
        List<AiClientApiVO> aiClientApiList = dynamicContext.getValue(this.dynamicDataKey());
        if (null == aiClientApiList || aiClientApiList.isEmpty()) {
            log.warn("没有需要被初始化的 AiClientApi 信息.");
            // 直接往下路由, 交给下一个节点
            return router(armoryCommandEntity, dynamicContext);
        }
        // 构建 AiClientApi
        aiClientApiList.forEach(aiClientApi -> {
            // 构建OpenAiApi builder
            OpenAiApi.Builder openAiApiBuilder = OpenAiApi.builder()
                    .baseUrl(aiClientApi.getBaseUrl())
                    .apiKey(aiClientApi.getApiKey());
            // 按需配置 embeddingsPath & completionsPath
            if (StringUtils.hasLength(aiClientApi.getEmbeddingsPath()) && StringUtils.hasLength(aiClientApi.getEmbeddingsPath().trim())) {
                openAiApiBuilder.embeddingsPath(aiClientApi.getEmbeddingsPath());
            }
            if (StringUtils.hasLength(aiClientApi.getCompletionsPath()) && StringUtils.hasLength(aiClientApi.getCompletionsPath().trim())) {
                openAiApiBuilder.completionsPath(aiClientApi.getCompletionsPath());
            }
            // 构建OpenAiApi实例
            OpenAiApi openAiApi = openAiApiBuilder.build();
            // 通过id注册bean对象
            this.registerBean(this.beanName(aiClientApi.getApiId()), OpenAiApi.class, openAiApi);
        });

        // 继续往下路由
        return router(armoryCommandEntity, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientToolMcpNode;
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_API.getBeanName(beanId);
    }

    @Override
    protected String dynamicDataKey() {
        return AiAgentEnumVO.AI_CLIENT_API.getDynamicDataKey();
    }
}
