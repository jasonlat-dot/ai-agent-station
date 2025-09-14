package com.jasonlat.domain.agent.service.execute.auto.step;

import com.alibaba.fastjson2.JSON;
import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.AutoAgentExecuteResultEntity;
import com.jasonlat.domain.agent.model.entity.ExecuteCommandEntity;
import com.jasonlat.domain.agent.model.valobj.AiAgentClientFlowConfigVO;
import com.jasonlat.domain.agent.model.valobj.enums.AiClientTypeEnumVO;
import com.jasonlat.domain.agent.service.execute.auto.step.factory.DefaultAutoAgentExecuteStrategyFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * @author jasonlat
 * 2025-09-10  20:56
 */
@Service
public class Step2PrecisionExecutorNode extends AbstractExecuteSupport{
    /**
     * 业务流程处理方法: 精准任务执行节点
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
    protected String doApply(ExecuteCommandEntity requestParameter, DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("\n⚡ 阶段2: Ai auto agent 精准任务执行");
        // 从动态上下文中获取分析结果
        String analysisResult = dynamicContext.getValue("analysisResult");
        if (analysisResult == null || analysisResult.trim().isEmpty()) {
            log.warn("⚠️ 分析结果为空，使用默认执行策略");
            analysisResult = "执行当前任务步骤";
        }

        String executionPrompt = String.format("""
                **用户原始需求:** %s
                
                **分析师策略:** %s
                
                **执行指令:** 你是一个精准任务执行器，需要根据用户需求和分析师策略，实际执行具体的任务。
                
                **执行要求:**
                1. 直接执行用户的具体需求（如搜索、检索、生成内容等）
                2. 如果需要搜索信息，请实际进行搜索和检索
                3. 如果需要生成计划、列表等，请直接生成完整内容
                4. 提供具体的执行结果，而不只是描述过程
                5. 确保执行结果能直接回答用户的问题
                
                **输出格式:**
                执行目标: [明确的执行目标]
                执行过程: [实际执行的步骤和调用的工具]
                执行结果: [具体的执行成果和获得的信息/内容]
                质量检查: [对执行结果的质量评估]
                """, requestParameter.getMessage(), analysisResult);

        // 获取对话客户端
        AiAgentClientFlowConfigVO aiAgentClientFlowConfigVO = dynamicContext.getAiAgentClientFlowConfigVOMap().get(AiClientTypeEnumVO.PRECISION_EXECUTOR_CLIENT.getCode());
        log.info("AiAgentClientFlowConfigVO INFO: {}", JSON.toJSONString(aiAgentClientFlowConfigVO));
        ChatClient chatClient = getChatClientByClientId(aiAgentClientFlowConfigVO.getClientId());

        String executionResult = chatClient
                .prompt(executionPrompt)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, requestParameter.getSessionId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1024))
                .call().content();

        parseExecutionResult(dynamicContext, executionResult, requestParameter.getSessionId());
        // 将执行结果保存到动态上下文中，供下一步使用
        dynamicContext.setValue("executionResult", executionResult);

        // 更新执行历史
        String stepSummary = String.format("""
                === 第 %d 步执行记录 ===
                【分析阶段】%s
                【执行阶段】%s
                """, dynamicContext.getStep(), analysisResult, executionResult);

        dynamicContext.getExecutionHistory().append(stepSummary);

        return router(requestParameter, dynamicContext);
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
    public StrategyHandler<ExecuteCommandEntity, DefaultAutoAgentExecuteStrategyFactory.DynamicContext, String> get(ExecuteCommandEntity requestParameter, DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return beanUtils.getBean("step3QualitySupervisorNode");
    }

    /**
     * 解析执行结果
     */
    private void parseExecutionResult(DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext, String executionResult, String sessionId) {
        int step = dynamicContext.getStep();
        log.info("\n⚡ === 第 {} 步执行结果 ===", step);

        if (executionResult == null || executionResult.trim().isEmpty()) {
            log.info("\n⚠️  未获取到执行结果");
            return;
        }

        String[] lines = executionResult.split("\n");
        String currentSection = "";
        StringBuilder sectionContent = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.contains("执行目标:")) {
                // 发送上一个section的内容
                sendExecutionSubResult(dynamicContext, currentSection, sectionContent.toString(), sessionId);
                currentSection = "execution_target";
                sectionContent = new StringBuilder();
                log.info("\n🎯 执行目标:");
                continue;
            } else if (line.contains("执行过程:")) {
                // 发送上一个section的内容
                sendExecutionSubResult(dynamicContext, currentSection, sectionContent.toString(), sessionId);
                currentSection = "execution_process";
                sectionContent = new StringBuilder();
                log.info("\n🔧 执行过程:");
                continue;
            } else if (line.contains("执行结果:")) {
                // 发送上一个section的内容
                sendExecutionSubResult(dynamicContext, currentSection, sectionContent.toString(), sessionId);
                currentSection = "execution_result";
                sectionContent = new StringBuilder();
                log.info("\n📈 执行结果:");
                continue;
            } else if (line.contains("质量检查:")) {
                // 发送上一个section的内容
                sendExecutionSubResult(dynamicContext, currentSection, sectionContent.toString(), sessionId);
                currentSection = "execution_quality";
                sectionContent = new StringBuilder();
                log.info("\n🔍 质量检查:");
                continue;
            }

            // 收集当前section的内容
            if (!currentSection.isEmpty()) {
                sectionContent.append(line).append("\n");
                switch (currentSection) {
                    case "execution_target":
                        log.info("   🎯 {}", line);
                        break;
                    case "execution_process":
                        log.info("   ⚙️ {}", line);
                        break;
                    case "execution_result":
                        log.info("   📊 {}", line);
                        break;
                    case "execution_quality":
                        log.info("   ✅ {}", line);
                        break;
                    default:
                        log.info("   📝 {}", line);
                        break;
                }
            }
        }

        // 发送最后一个section的内容
        sendExecutionSubResult(dynamicContext, currentSection, sectionContent.toString(), sessionId);
    }

    /**
     * 发送执行阶段细分结果到流式输出
     */
    private void sendExecutionSubResult(DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext,
                                        String subType, String content, String sessionId) {
        // 抽取的通用判断逻辑
        if (!subType.isEmpty() && !content.isEmpty()) {
            AutoAgentExecuteResultEntity result = AutoAgentExecuteResultEntity.createExecutionSubResult(
                    dynamicContext.getStep(), subType, content, sessionId);
            sendSseResult(dynamicContext, result);
        }
    }

}
