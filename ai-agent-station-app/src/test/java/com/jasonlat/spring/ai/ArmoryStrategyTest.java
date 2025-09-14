package com.jasonlat.spring.ai;


import cn.hutool.core.text.StrBuilder;
import com.alibaba.fastjson.JSON;
import com.jasonlat.design.framework.tree.StrategyHandler;
import com.jasonlat.domain.agent.model.entity.ArmoryCommandEntity;
import com.jasonlat.domain.agent.model.valobj.enums.AiAgentEnumVO;
import com.jasonlat.domain.agent.service.armory.bussiness.factory.DefaultArmoryStrategyFactory;
import com.jasonlat.types.utils.BeanUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static com.jasonlat.spring.ai.AiAgentTest.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static com.jasonlat.spring.ai.AiAgentTest.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArmoryStrategyTest {

    @Autowired
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;

    @Test
    public void testArmoryStrategy() throws Exception {

        List<String> clientIdList = List.of("3001");

        StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> strategyHandler = defaultArmoryStrategyFactory.armoryStrategyHandler();
        strategyHandler.apply(ArmoryCommandEntity.builder().commandType(AiAgentEnumVO.AI_CLIENT.getCode()).commandIdList(clientIdList).build(), new DefaultArmoryStrategyFactory.DynamicContext());
    }


    @Resource
    private ApplicationContext applicationContext;

    @Test
    public void test_aiClientApiNode() throws Exception {
        StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> armoryStrategyHandler =
                defaultArmoryStrategyFactory.armoryStrategyHandler();

        String apply = armoryStrategyHandler.apply(
                ArmoryCommandEntity.builder()
                        .commandType(AiAgentEnumVO.AI_CLIENT.getCode())
                        .commandIdList(Arrays.asList("3001"))
                        .build(),
                new DefaultArmoryStrategyFactory.DynamicContext());

        OpenAiApi openAiApi = (OpenAiApi) applicationContext.getBean(AiAgentEnumVO.AI_CLIENT_API.getBeanName("1001"));

        log.info("测试结果：{}", openAiApi);
    }


    /**
     * win 需要修改 npx 为绝对路径
     * {
     *     "filesystem": {
     *         "command": "C:\\Users\\Administrator\\AppData\\Roaming\\npm\\npx.cmd",
     *         "args": [
     *             "-y",
     *             "@modelcontextprotocol/server-filesystem",
     *             "C:\\Users\\Administrator\\Desktop",
     *             "C:\\Users\\Administrator\\Desktop"
     *         ]
     *     }
     * }
     */
    @Test
    public void test_aiClientModelNode() throws Exception {
        StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> armoryStrategyHandler =
                defaultArmoryStrategyFactory.armoryStrategyHandler();

        String apply = armoryStrategyHandler.apply(
                ArmoryCommandEntity.builder()
                        .commandType(AiAgentEnumVO.AI_CLIENT.getCode())
                        .commandIdList(Arrays.asList("3001"))
                        .build(),
                new DefaultArmoryStrategyFactory.DynamicContext());

        OpenAiChatModel openAiChatModel = (OpenAiChatModel) applicationContext.getBean(AiAgentEnumVO.AI_CLIENT_MODEL.getBeanName("2001"));

        log.info("模型构建:{}", openAiChatModel);

        // 1. 有哪些工具可以使用
        // 2. 在 C:\Users\Administrator\Desktop 创建 txt.md 文件
        Prompt prompt = Prompt.builder()
                .messages(new UserMessage(
                        """
                                在 C:\\Users\\Administrator\\Desktop 创建 txt_2.md 文件
                                """))
                .build();

        ChatResponse chatResponse = openAiChatModel.call(prompt);

        log.info("测试结果(call):{}", JSON.toJSONString(chatResponse));
    }

    @Test
    public void test_aiClient() throws Exception {
        StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> armoryStrategyHandler =
                defaultArmoryStrategyFactory.armoryStrategyHandler();

        String apply = armoryStrategyHandler.apply(
                ArmoryCommandEntity.builder()
                        .commandType(AiAgentEnumVO.AI_CLIENT.getCode())
                        .commandIdList(Arrays.asList("3001"))
                        .build(),
                new DefaultArmoryStrategyFactory.DynamicContext());

        ChatClient chatClient = (ChatClient) applicationContext.getBean(AiAgentEnumVO.AI_CLIENT.getBeanName("3001"));
        log.info("客户端构建:{}", chatClient);

        String content = chatClient.prompt(Prompt.builder()
                .messages(new UserMessage(
                        """
                                有哪些工具可以使用
                                """))
                .build()).call().content();

        log.info("测试结果(call):{}", content);
    }


    @Resource
    private BeanUtils beanUtils;
    @Test
    public void test_aiClient_prompt() throws Exception {

        String analysisPrompt = String.format("""
                        **原始用户需求:** %s
                        
                        **当前执行步骤:** 第 %d 步 (最大 %d 步)
                        
                        **历史执行记录:**
                        %s
                        
                        **当前任务:** %s
                        
                        **分析要求:**
                        请深入分析用户的具体需求，制定明确的执行策略：
                        1. 理解用户真正想要什么（如：具体的学习计划、项目列表、技术方案等）
                        2. 分析需要哪些具体的执行步骤（如：搜索信息、检索项目、生成内容等）
                        3. 制定能够产生实际结果的执行策略
                        4. 确保策略能够直接回答用户的问题
                        
                        **输出格式要求:**
                        任务状态分析: [当前任务完成情况的详细分析]
                        执行历史评估: [对已完成工作的质量和效果评估]
                        下一步策略: [具体的执行计划，包括需要调用的工具和生成的内容]
                        完成度评估: [0-100]%%
                        任务状态: [CONTINUE/COMPLETED]
                        """,
                "1+1",
                5,
                10,
                "[首次执行]",
                "第一次执行"
        );

        ChatClient chatClient = getChatClientByClientId("3101");

        // 分析用户需求，获取分析结果
        log.info("🎯 分析用户需求 begin");
        Flux<String> fluxString = chatClient
                .prompt(analysisPrompt)
                .advisors(
                        a -> a
                                .param(CHAT_MEMORY_CONVERSATION_ID_KEY, "1111111")
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1024))
                .stream()
                .content()
                ;

        // 2. 实时打印：每拿到一个片段就打印（核心逻辑）
        StrBuilder result = new StrBuilder();
        fluxString
                // doOnNext()：流中每产出一个元素，就执行一次打印
                .doOnNext(chunk -> {
                    result.append(chunk);
                    System.out.print(chunk);
                })
                // 可选：流结束时打印提示
                .doOnComplete(() -> System.out.println("\n\n流式传输全部完成！"))
                // 非 Web 环境必需：阻塞主线程，等待流结束（否则程序会提前退出）
                .blockLast();

        // 3. 订阅流式结果（核心步骤）


        log.info("🎯 分析用户需求 end, {}", result.toString());
    }

    /**
     * 根据客户端ID获取ChatClient实例
     *
     * @param clientId 客户端ID
     * @return ChatClient实例
     */
    protected ChatClient getChatClientByClientId(String clientId) {
        return beanUtils.getBean(AiAgentEnumVO.AI_CLIENT.getBeanName(clientId));
    }


}
