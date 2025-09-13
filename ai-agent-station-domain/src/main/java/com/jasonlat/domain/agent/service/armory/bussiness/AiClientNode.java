package com.jasonlat.domain.agent.service.armory.bussiness;

import cn.hutool.core.text.StrBuilder;
import com.alibaba.fastjson2.JSON;
import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.jasonlat.domain.agent.model.valobj.AiClientSystemPromptVO;
import com.jasonlat.domain.agent.model.valobj.AiClientVO;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiClientNode extends AbstractArmorySupport {

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
        log.info("Ai agent Client 客户端构建节点: {}", JSON.toJSONString(requestParameter));

        List<AiClientVO> aiClientList = dynamicContext.getValue(this.dynamicDataKey());
        if (null == aiClientList || aiClientList.isEmpty()) {
            return router(requestParameter, dynamicContext);
        }

        // 获取对话提示词
        Map<String, AiClientSystemPromptVO> systemPromptsMap = dynamicContext.getValue(AiAgentEnumVO.AI_CLIENT_SYSTEM_PROMPT.getDynamicDataKey());
        aiClientList.forEach(aiClient -> {

            // 获取对话模型
            OpenAiChatModel chatModel = beanUtils.getBean(aiClient.getModelBeanName());
            if (null == chatModel) {
                throw new RuntimeException("AI客户端【clientId is + " + aiClient.getClientId() + "】配置错误: client未正确配置model模型");
            }

            // 构建对话客户端
            ChatClient.Builder chatClientBuilder = ChatClient.builder(chatModel);

            // 加载预设话术
            StrBuilder defaultSystemPrompt = this.getDefaultSystemPrompt(aiClient, systemPromptsMap);
            chatClientBuilder.defaultSystem(defaultSystemPrompt.toString());

            // 构建MCP服务 -> MCP本系统配置在了chatModel上，如果有需求在Client配置，代码如下

//            List<String> mcpBeanNameList = aiClient.getMcpBeanNameList();
//            if (null != mcpBeanNameList && !mcpBeanNameList.isEmpty()) {
//                List<McpSyncClient> mcpSyncClients = mcpBeanNameList.stream()
//                        .map(beanName -> this.getBean(beanName, McpSyncClient.class))
//                        .toList();
//                chatClientBuilder.defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients));
//            }

            // 构建顾问角色
            List<String> advisorBeanNameList = aiClient.getAdvisorBeanNameList();
            if (null != advisorBeanNameList && !advisorBeanNameList.isEmpty()) {
                List<Advisor> advisors = advisorBeanNameList.stream().map(beanName -> beanUtils.getBean(beanName, Advisor.class)).toList();
                chatClientBuilder.defaultAdvisors(advisors);
            }

            // 构建对话客户端 & 注册 bean
            ChatClient client = chatClientBuilder.build();
            beanUtils.registerBean(this.beanName(aiClient.getClientId()), ChatClient.class, client);
        });
        return "Ai Client Build Completed!";
    }

    private StrBuilder getDefaultSystemPrompt(AiClientVO aiClient, Map<String, AiClientSystemPromptVO> systemPromptsMap) {
        StrBuilder defaultSystemPrompt = new StrBuilder("Ai 智能体 \r\n");
        if (null == systemPromptsMap || systemPromptsMap.isEmpty()) {
            return defaultSystemPrompt;
        }
        List<String> promptIdList = aiClient.getPromptIdList();
        promptIdList.forEach(promptId -> {
            AiClientSystemPromptVO aiClientSystemPromptVO = systemPromptsMap.get(promptId);
            if (null == aiClientSystemPromptVO) {
                log.warn("Ai agent Client 预设话术不存在, promptId: {}", promptId);
                return;
            }
            defaultSystemPrompt.append(aiClientSystemPromptVO.getPromptContent());
        });
        return defaultSystemPrompt;
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
        return super.get(requestParameter, dynamicContext);
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT.getBeanName(beanId);
    }

    @Override
    protected String dynamicDataKey() {
        return AiAgentEnumVO.AI_CLIENT.getDynamicDataKey();
    }
}
