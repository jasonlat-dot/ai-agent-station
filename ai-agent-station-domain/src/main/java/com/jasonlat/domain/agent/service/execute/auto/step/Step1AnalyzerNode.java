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
 * 2025-09-10  20:48
 */
@Service
public class Step1AnalyzerNode extends AbstractExecuteSupport {

    /**
     * 业务流程处理方法: 步骤1分析节点，分析用户输入的内容，并返回分析结果
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
        log.info("\n🎯 === 执行第 {} 步 ===", dynamicContext.getStep());

        // 第一阶段：任务分析
        log.info("\n📊 阶段1: 任务状态分析");
        String analysisPrompt = String.format("""
                        **原始用户需求:** %s
                        
                        **当前执行步骤:** 第 %d 步 (最大 %d 步)
                        
                        **历史执行记录:**
                        %s
                        
                        **当前任务:** %s
                        
                        请分析当前任务状态，评估执行进度，并制定下一步策略。
                        """,
                requestParameter.getMessage(),
                dynamicContext.getStep(),
                dynamicContext.getMaxStep(),
                !dynamicContext.getExecutionHistory().isEmpty() ? dynamicContext.getExecutionHistory().toString() : "[首次执行]",
                dynamicContext.getCurrentTask()
        );

        AiAgentClientFlowConfigVO aiAgentClientFlowConfigVO = dynamicContext.getAiAgentClientFlowConfigVOMap().get(AiClientTypeEnumVO.TASK_ANALYZER_CLIENT.getCode());
        log.info("AiAgentClientFlowConfigVO INFO: {}", JSON.toJSONString(aiAgentClientFlowConfigVO));
        ChatClient chatClient = getChatClientByClientId(aiAgentClientFlowConfigVO.getClientId());

        // 分析用户需求，获取分析结果
        String analysisResult = chatClient
                .prompt(analysisPrompt)
                .advisors(a -> a
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, requestParameter.getSessionId())
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1024))
                .call().content();

        // 解析分析结果
        parseAnalysisResult(dynamicContext.getStep(), analysisResult);

        // 将分析结果保存到动态上下文中，供下一步使用
        dynamicContext.setValue("analysisResult", analysisResult);

        // 检查是否已完成
        if ( analysisResult != null && (analysisResult.contains("任务状态: COMPLETED") || analysisResult.contains("完成度评估: 100%"))) {
            dynamicContext.setCompleted(true);
            log.info("✅ 任务分析显示已完成！");
            return router(requestParameter, dynamicContext);
        }

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
        // 如果任务已完成或达到最大步数，进入总结阶段
        if (dynamicContext.isCompleted() || dynamicContext.getStep() > dynamicContext.getMaxStep()) {
            return beanUtils.getBean("step4LogExecutionSummaryNode");
        }

        // 否则继续执行下一步
        return beanUtils.getBean("step2PrecisionExecutorNode");
    }

    private void parseAnalysisResult(int step, String analysisResult) {
        log.info("\n📊 === 第 {} 步分析结果 ===", step);
        if (analysisResult == null  || analysisResult.trim().isEmpty()) {
            log.info("\n⚠️  未获取到分析结果");
            return;
        }
        String[] lines = analysisResult.split("\n");
        String currentSection = "";

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.contains("任务状态分析:")) {
                currentSection = "status";
                log.info("\n🎯 任务状态分析:");
                continue;
            } else if (line.contains("执行历史评估:")) {
                currentSection = "history";
                log.info("\n📈 执行历史评估:");
                continue;
            } else if (line.contains("下一步策略:")) {
                currentSection = "strategy";
                log.info("\n🚀 下一步策略:");
                continue;
            } else if (line.contains("完成度评估:")) {
                currentSection = "progress";
                String progress = line.substring(line.indexOf(":") + 1).trim();
                log.info("\n📊 完成度评估: {}", progress);
                continue;
            } else if (line.contains("任务状态:")) {
                currentSection = "task_status";
                String status = line.substring(line.indexOf(":") + 1).trim();
                if (status.equals("COMPLETED")) {
                    log.info("\n✅ 任务状态: 已完成");
                } else {
                    log.info("\n🔄 任务状态: 继续执行");
                }
                continue;
            }

            switch (currentSection) {
                case "status":
                    log.info("   📋 {}", line);
                    break;
                case "history":
                    log.info("   📊 {}", line);
                    break;
                case "strategy":
                    log.info("   🎯 {}", line);
                    break;
                default:
                    log.info("   📝 {}", line);
                    break;
            }
        }
    }
}
