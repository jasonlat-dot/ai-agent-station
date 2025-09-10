package com.jasonlat.domain.agent.service.execute.auto.step;

import com.alibaba.fastjson2.JSON;
import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ExecuteCommandEntity;
import com.jasonlat.domain.agent.model.valobj.AiAgentClientFlowConfigVO;
import com.jasonlat.domain.agent.model.valobj.enums.AiClientTypeEnumVO;
import com.jasonlat.domain.agent.service.execute.auto.step.factory.DefaultAutoAgentExecuteStrategyFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * @author jasonlat
 * 2025-09-10  21:05
 */
@Service
public class Step4LogExecutionSummaryNode extends AbstractExecuteSupport {
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
    protected String doApply(ExecuteCommandEntity requestParameter, DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("\n📊 === 执行第 {} 步 ===", dynamicContext.getStep());

        // 第四阶段：执行总结
        log.info("\n📊 阶段4: 执行总结分析");

        // 记录执行总结
        logExecutionSummary(dynamicContext.getMaxStep(), dynamicContext.getExecutionHistory(), dynamicContext.isCompleted());

        // 如果任务未完成，生成最终总结报告
        if (!dynamicContext.isCompleted()) {
            generateFinalReport(requestParameter, dynamicContext);
        }

        log.info("\n🏁 === 动态多轮执行测试结束 ====");

        return "ai agent execution summary completed!";
    }


    /**
     * 记录执行总结
     */
    private void logExecutionSummary(int maxSteps, StringBuilder executionHistory, boolean isCompleted) {
        log.info("\n📊 === 动态多轮执行总结 ====");

        int actualSteps = Math.min(maxSteps, executionHistory.toString().split("=== 第").length - 1);
        log.info("📈 总执行步数: {} 步", actualSteps);

        if (isCompleted) {
            log.info("✅ 任务完成状态: 已完成");
        } else {
            log.info("⏸️ 任务完成状态: 未完成（达到最大步数限制）");
        }

        // 计算执行效率
        double efficiency = isCompleted ? 100.0 : (double) actualSteps / maxSteps * 100;
        log.info("📊 执行效率: {}",  String.format("%.1f%%", efficiency));
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
        return super.get(requestParameter, dynamicContext);
    }


    /**
     * 生成最终总结报告
     */
    private void generateFinalReport(ExecuteCommandEntity requestParameter, DefaultAutoAgentExecuteStrategyFactory.DynamicContext dynamicContext) {
        try {
            log.info("\n--- 生成未完成任务的总结报告 ---");

            String summaryPrompt = String.format("""
                    请对以下未完成的任务执行过程进行总结分析：
                    
                    **原始用户需求:** %s
                    
                    **执行历史:**
                    %s
                    
                    **分析要求:**
                    1. 总结已完成的工作内容
                    2. 分析未完成的原因
                    3. 提出完成剩余任务的建议
                    4. 评估整体执行效果
                    """,
                    requestParameter.getMessage(),
                    dynamicContext.getExecutionHistory().toString());

            // 获取对话客户端 - 使用任务分析客户端进行总结
            AiAgentClientFlowConfigVO aiAgentClientFlowConfigVO = dynamicContext.getAiAgentClientFlowConfigVOMap().get(AiClientTypeEnumVO.TASK_ANALYZER_CLIENT.getCode());
            log.info("AiAgentClientFlowConfigVO INFO: {}", JSON.toJSONString(aiAgentClientFlowConfigVO));
            ChatClient chatClient = getChatClientByClientId(aiAgentClientFlowConfigVO.getClientId());

            String summaryResult = chatClient
                    .prompt(summaryPrompt)
                    .advisors(a -> a
                            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, requestParameter.getSessionId() + "-summary")
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 50))
                    .call().content();

            logFinalReport(summaryResult);

            // 将总结结果保存到动态上下文中
            dynamicContext.setValue("finalSummary", summaryResult);

        } catch (Exception e) {
            log.error("生成最终总结报告时出现异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 输出最终总结报告
     */
    private void logFinalReport(String summaryResult) {
        log.info("\n📋 === 最终总结报告 ===");

        if (summaryResult == null || summaryResult.isEmpty() || summaryResult.trim().isEmpty()) {
            log.info("\n⚠️  未获取到总结结果");
            return;
        }

        String[] lines = summaryResult.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // 根据内容类型添加不同图标
            if (line.contains("已完成") || line.contains("完成的工作")) {
                log.info("✅ {}", line);
            } else if (line.contains("未完成") || line.contains("原因")) {
                log.info("❌ {}", line);
            } else if (line.contains("建议") || line.contains("推荐")) {
                log.info("💡 {}", line);
            } else if (line.contains("评估") || line.contains("效果")) {
                log.info("📊 {}", line);
            } else {
                log.info("📝 {}", line);
            }
        }
    }

}
