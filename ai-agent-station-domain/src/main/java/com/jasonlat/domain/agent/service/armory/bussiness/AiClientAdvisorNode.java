package com.jasonlat.domain.agent.service.armory.bussiness;

import com.alibaba.fastjson2.JSON;
import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.jasonlat.domain.agent.model.valobj.enums.AiClientAdvisorTypeEnumVO;
import com.jasonlat.domain.agent.model.valobj.AiClientAdvisorVO;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class AiClientAdvisorNode extends AbstractArmorySupport {

//    @Resource(name = "ollamaSimpleVectorStore")
    @Resource(name = "openaiPgVectorStore")
    private VectorStore vectorStore;

    @Resource
    private AiClientNode aiClientNode;

    /**
     * 业务流程处理方法
     * <p>
     * 子类需要实现此方法来定义具体的业务处理逻辑。
     * 该方法在异步数据加载完成后执行。
     * </p>
     *
     * @param requestParameter 请求参数
     * @param dynamicContext   动态上下文
     * @return 处理结果
     * @throws Exception 处理过程中可能抛出的异常
     */
    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai agent Advisor 顾问角色构建节点: {}", JSON.toJSONString(requestParameter));

        List<AiClientAdvisorVO> aiClientAdvisors = dynamicContext.getValue(this.dynamicDataKey());
        if (aiClientAdvisors == null || aiClientAdvisors.isEmpty()) {
            log.warn("没有需要被初始化的 AiClientAdvisor 信息.");
            return router(requestParameter, dynamicContext);
        }

        aiClientAdvisors.forEach(aiClientAdvisorVO -> {
            // 创建顾问对象
            Advisor aiClientAdvisor = createAiClientAdvisor(aiClientAdvisorVO);
            // 注册顾问对象
            beanUtils.registerBean(this.beanName(aiClientAdvisorVO.getAdvisorId()), Advisor.class, aiClientAdvisor);
        });

        return router(requestParameter, dynamicContext);
    }

    private Advisor createAiClientAdvisor(AiClientAdvisorVO aiClientAdvisorVO) {
        String advisorType = aiClientAdvisorVO.getAdvisorType();
        AiClientAdvisorTypeEnumVO advisorTypeEnumVO = AiClientAdvisorTypeEnumVO.getByCode(advisorType);
        return advisorTypeEnumVO.createAdvisor(aiClientAdvisorVO, vectorStore);
    }

    /**
     * 获取待执行的策略处理器
     * <p>
     * 根据请求参数和动态上下文的内容，选择并返回合适的策略处理器。
     * 实现类需要根据具体的业务规则来实现策略选择逻辑。
     * </p>
     *
     * @param requestParameter 请求参数，用于确定策略选择的依据
     * @param dynamicContext   动态上下文，包含策略选择过程中需要的额外信息
     * @return 选择的策略处理器，如果没有找到合适的策略则返回null
     * @throws Exception 策略选择过程中可能抛出的异常
     */
    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientNode;
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_ADVISOR.getBeanName(beanId);
    }

    @Override
    protected String dynamicDataKey() {
        return AiAgentEnumVO.AI_CLIENT_ADVISOR.getDynamicDataKey();
    }
}
